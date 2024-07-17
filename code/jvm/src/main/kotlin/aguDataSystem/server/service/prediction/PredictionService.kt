package aguDataSystem.server.service.prediction

import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.alerts.AlertCreationDTO
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.Transaction
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.alerts.AlertsService
import aguDataSystem.server.service.chron.FetchService
import aguDataSystem.server.service.chron.models.prediction.ConsumptionRequestModel
import aguDataSystem.server.service.chron.models.prediction.TemperatureRequestModel
import aguDataSystem.server.service.prediction.models.PredictionResponseModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToInt
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for the prediction of gas consumption of the AGUs.
 */
@Service
class PredictionService(
	private val transactionManager: TransactionManager,
	private val fetchService: FetchService,
	private val alertsService: AlertsService
) {

	/**
	 * Schedules the training chron task.
	 *
	 * Still sketchy, needs to be implemented
	 */
	fun trainAGUs() {
		val aguList = transactionManager.run {
			it.aguRepository.getAGUsBasicInfo()
		}
		aguList.forEach { agu ->
			trainAGU(agu)
		}
	}

	/**
	 * Trains the model for an AGU
	 *
	 * @param agu The AGU to train the model for
	 */
	private fun trainAGU(agu: AGUBasicInfo) {
		var temps: List<TemperatureRequestModel> = emptyList()
		var consumptions: List<ConsumptionRequestModel> = emptyList()
		transactionManager.run { tm ->
			logger.info("Fetching providers for AGU for training model: {}", agu.cui)
			val aguProviders = tm.providerRepository.getProviderByAGU(agu.cui)

			logger.info("Fetching temperature and gas measures for AGU: {}", agu.cui)
			aguProviders.forEach { provider ->
				when (provider.getProviderType()) {
					ProviderType.TEMPERATURE -> temps = getTemperaturePredictions(tm, provider.id, false)
					ProviderType.GAS -> consumptions = fetchGasConsumptions(tm, provider.id)
				}
			}
		}
		if (temps.size < NUMBER_OF_DAYS || consumptions.size < NUMBER_OF_DAYS) {
			logger.error("Not enough data to train AGU: {}", agu.cui)
			return
		}

		logger.info("Generating training model for AGU: {}", agu.cui)
		val training = fetchService.generateTraining(temps, consumptions)

		if (training != null) {
			transactionManager.run {
				logger.info("Saving training model for AGU: {}", agu.cui)
				it.aguRepository.updateTrainingModel(agu.cui, training)
			}
		} else {
			logger.error("Failed to generate training model for AGU: {}", agu.cui)
		}
	}

	/**
	 * Receives an AGU and gets/stores the predictions for the next n days and calculate when loads are needed for this AGU
	 *
	 * @param agu The AGU to process
	 */
	fun processAGU(agu: AGUBasicInfo) {

		transactionManager.run { transaction ->

			logger.info("Fetching providers for AGU: {}", agu.cui)
			val temperatureProvider =
				transaction.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.TEMPERATURE)
					?: throw Exception("No temperature provider found for AGU: ${agu.cui}")
			val gasProvider = transaction.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
				?: throw Exception("No gas provider found for AGU: ${agu.cui}")

			logger.info("Fetching temperature, and gas consumptions for AGU: {}", agu.cui)
			val temps = getTemperaturePredictions(transaction, temperatureProvider.id, true)
			val consumptions = fetchGasConsumptions(transaction, gasProvider.id)

			if (temps.size < (NUMBER_OF_DAYS + TEMP_NUMBER_OF_DAYS) || consumptions.size < NUMBER_OF_DAYS) {
				logger.warn("Not enough data to predict consumption for AGU: {}", agu.cui)
				return@run
			}

			logger.info("Fetching training model for AGU: {}", agu.cui)
			val training = transaction.aguRepository.getTraining(agu.cui)

			if (training == null) {
				logger.warn("No training model found for AGU: {}", agu.cui)
				return@run
			}

			logger.info("Generating gas consumption predictions for AGU: {}", agu.cui)
			val predictions = fetchService.generatePredictions(
				futureTemps = temps, consumptions = consumptions, training = training
			)

			if (predictions.isNotEmpty()) {
				val completeAGU =
					transaction.aguRepository.getAGUByCUI(agu.cui) ?: throw Exception("AGU not found: ${agu.cui}")

				val predictedLevels: List<Int>
				//TODO: We need tests for small AGU and big AGU
				if (completeAGU.loadVolume > BIG_AGU_LIMIT_LOAD_VOLUME) {
					predictedLevels = manageLoads(transaction, agu, predictions)
				} else {
					logger.info("AGU {} is to small to have automatic scheduling of loads", agu.cui)
					val currentLevel =
						transaction.gasRepository.getLatestLevels(agu.cui, gasProvider.id).sumOf { it.level }
					predictedLevels = mutableListOf()
					predictions.forEachIndexed { index, prediction ->
						var totalLevel = if (index == 0) currentLevel else predictedLevels[index - 1]
						totalLevel = (totalLevel - prediction.consumption).roundToInt()
						predictedLevels.add(totalLevel)
					}
				}
				savePredictions(transaction, agu, predictedLevels, gasProvider.id)
			} else {
				logger.error("Failed to generate gas consumption predictions for AGU: {}", agu.cui)
			}
		}
	}

	/**
	 * Fetches the gas consumptions for the AGU
	 *
	 * @param transaction the transaction to use
	 * @param providerId the ID of the gas provider
	 *
	 * @return the gas consumptions
	 */
	private fun fetchGasConsumptions(transaction: Transaction, providerId: Int): List<ConsumptionRequestModel> {
		val consumptionList = transaction.gasRepository.getGasMeasures(providerId, NUMBER_OF_DAYS + 1, LocalTime.MIDNIGHT).toMutableList()

		if (consumptionList.size < NUMBER_OF_DAYS + 1) {
			val lastGasMeasure = consumptionList.lastOrNull()
			if (lastGasMeasure != null) {
				var lastLevel = lastGasMeasure.level
				var daysToFill = (NUMBER_OF_DAYS + 1) - consumptionList.size
				while (daysToFill > 0) {
					lastLevel = (lastLevel * 0.95).toInt()
					consumptionList.add(
						GasMeasure(
							timestamp = lastGasMeasure.timestamp.plusDays(consumptionList.size.toLong() - lastGasMeasure.timestamp.toLocalDate().toEpochDay()),
							level = lastLevel,
							predictionFor = lastGasMeasure.predictionFor.plusDays(consumptionList.size.toLong() - lastGasMeasure.timestamp.toLocalDate().toEpochDay()),
							tankNumber = lastGasMeasure.tankNumber,
						)
					)
					daysToFill--
				}
			} else {
				logger.error("No gas measures found for provider: {}", providerId)
				return emptyList()
			}
		}

		return consumptionList.map { gasMeasure -> gasMeasure.level.toDouble() } // reduce the error
			.zipWithNext { a, b -> b - a } // calculate the consumption for each day
			.mapIndexed { idx, consumption ->
				ConsumptionRequestModel(
					consumption.roundToInt(), consumptionList[idx].timestamp.toLocalDate().minusDays(idx.toLong())
				)
			}.also { logger.info("Gas Consumptions: {}", it) }
	}

	/**
	 * Gets the temperature predictions for a provider needed for the prediction microservice.
	 *
	 * @param tm The transaction manager
	 * @param providerId The provider id
	 * @return The temperature predictions
	 */
	private fun getTemperaturePredictions(
		tm: Transaction,
		providerId: Int,
		prediction: Boolean
	): List<TemperatureRequestModel> {
		val previousTemps = tm.temperatureRepository.getTemperatureMeasures(providerId, NUMBER_OF_DAYS).filter {
			it.timestamp.toLocalDate() == it.predictionFor.toLocalDate()
		}.mapIndexed{ idx, temp ->
			TemperatureRequestModel(
				min = temp.min, max = temp.max, timeStamp = temp.timestamp.toLocalDate().minusDays(idx.toLong())//TODO: MARTELADO À CARA PODRE
			)
		}.toMutableList()

		if (previousTemps.size < NUMBER_OF_DAYS) {
			val lastTemp = previousTemps.lastOrNull()
			if (lastTemp != null) {
				var daysToFill = NUMBER_OF_DAYS - previousTemps.size
				var decrement = 1L
				while (daysToFill > 0) {
					previousTemps.add(
						TemperatureRequestModel(
							min = lastTemp.min,
							max = lastTemp.max,
							timeStamp = LocalDate.parse(lastTemp.timeStamp).minusDays(decrement)
						)
					)
					daysToFill--
					decrement++
				}
			} else {
				logger.error("No temperature measures found for provider: {}", providerId)
				return emptyList()
			}
		}

		if (!prediction) {
			return previousTemps
		}
		val futureTemps =
			tm.temperatureRepository.getPredictionTemperatureMeasures(providerId, TEMP_NUMBER_OF_DAYS).map { temp ->
				TemperatureRequestModel(
					min = temp.min, max = temp.max, timeStamp = temp.predictionFor.toLocalDate()
				)
			}.toMutableList()

		if (futureTemps.size < TEMP_NUMBER_OF_DAYS) {
			val lastTemp = futureTemps.lastOrNull()
			if (lastTemp != null) {
				var daysToFill = TEMP_NUMBER_OF_DAYS - futureTemps.size
				var increment = 1L
				while (daysToFill > 0) {
					futureTemps.add(
						TemperatureRequestModel(
							min = lastTemp.min,
							max = lastTemp.max,
							timeStamp = LocalDate.parse(lastTemp.timeStamp).plusDays(increment)
						)
					)
					daysToFill--
					increment++
				}
			} else {
				logger.error("No temperature predictions found for provider: {}", providerId)
				return previousTemps
			}
		}

		return previousTemps + futureTemps
	}

	/**
	 * Manages the loads for the AGU
	 * Either schedules a load if the predicted level is below the minimum level or removes a load if it's not needed anymore
	 *
	 * @param transaction the transaction to use
	 * @param agu the AGU to manage the loads for
	 * @param predictions the predictions to manage the loads for
	 *
	 * @return the predicted levels after taking the loads into account
	 */
	private fun manageLoads(
		transaction: Transaction,
		agu: AGUBasicInfo,
		predictions: List<PredictionResponseModel>
	): List<Int> {
		val fullAGU = transaction.aguRepository.getAGUByCUI(agu.cui) ?: throw Exception("AGU not found: ${agu.cui}")
		val minLevel = fullAGU.levels.min
		val gasProvider = transaction.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
			?: throw Exception("No gas provider found for AGU: ${agu.cui}")
		val currentLevel = transaction.gasRepository.getLatestLevels(agu.cui, gasProvider.id).sumOf { it.level }
		val predictedLevels = mutableListOf<Int>()

		predictions.forEachIndexed { index, prediction ->
			val date = LocalDate.now().plusDays(index.toLong())
			var totalLevel = if (index == 0) currentLevel else predictedLevels[index - 1]
			totalLevel = (totalLevel - prediction.consumption).roundToInt()

			val loadForDay = transaction.loadRepository.getLoadForDay(agu.cui, date)
			val loadAmount = loadForDay?.amount ?: 0.0
			val percentageAmountOfLoad = (fullAGU.loadVolume * loadAmount).toInt()

			totalLevel += percentageAmountOfLoad
			if (totalLevel > (minLevel + LOAD_REMOVAL_MARGIN) && loadForDay != null) {
				transaction.loadRepository.removeLoad(loadForDay.id)
				// Call in alert saying we removed a load because it was not needed (we can also put the details about the AGU and the load)
				alertsService.createAlert(
					AlertCreationDTO(
						aguId = agu.cui,
						title = "Load removed",
						message = "A load was removed because it was not needed"
					)
				)
				totalLevel -= percentageAmountOfLoad
			} else if (totalLevel < minLevel) {
				if (loadForDay != null) {
					// Call in alert because even with a load in said day the level is below the minimum threshold
					alertsService.createAlert(
						AlertCreationDTO(
							aguId = agu.cui, title = "Gas level", message = "Gas level is below the minimum threshold"
						)
					)
				} else {
					val adjustedDate = adjustForWeekend(date)
					transaction.loadRepository.scheduleLoad(
						ScheduledLoadCreationDTO(
							aguCui = agu.cui, date = adjustedDate, isManual = false
						)
					)
					totalLevel += fullAGU.loadVolume
				}
			}
			predictedLevels.add(totalLevel)
		}
		return predictedLevels
	}

	/**
	 * Saves the predicted levels obtained in the manageLoads function to the database.
	 * The obtained levels are distributed among the tanks of the AGU in proportion to their capacity
	 *
	 * @param transaction the transaction to use
	 * @param agu the AGU to save the predictions for
	 * @param predictedLevels the predicted levels to save
	 * @param gasProviderId the ID of the gas provider
	 */
	private fun savePredictions(
		transaction: Transaction, agu: AGUBasicInfo, predictedLevels: List<Int>, gasProviderId: Int
	) {
		val tanks = transaction.tankRepository.getAGUTanks(agu.cui)

		if (tanks.isEmpty()) {
			logger.error("No tanks found for AGU: {}", agu.cui)
			return
		}

		val totalCapacity = tanks.sumOf { it.capacity }

		predictedLevels.forEachIndexed { index, predictedLevel ->
			val tankLevels = mutableListOf<GasMeasure>()
			tanks.forEach { tank ->
				val tankLevel = (predictedLevel * tank.capacity / totalCapacity)
				tankLevels.add(
					GasMeasure(
						timestamp = LocalDateTime.now(),
						predictionFor = LocalDateTime.now().plusDays(index.toLong()),
						level = tankLevel,
						tankNumber = tank.number
					)
				)
			}
			transaction.gasRepository.addGasMeasuresToProvider(gasProviderId, tankLevels)
		}
	}

	/**
	 * Adjusts the date to the previous Friday if it's a Saturday or Sunday
	 *
	 * @param date the date to adjust
	 *
	 * @return the adjusted date
	 */
	private fun adjustForWeekend(date: LocalDate): LocalDate {
		return when (date.dayOfWeek) {
			DayOfWeek.SATURDAY -> date.minusDays(1)
			DayOfWeek.SUNDAY -> date.minusDays(2)
			else -> date
		}
	}

	companion object {
		private val logger = LoggerFactory.getLogger(FetchService::class.java)
		const val NUMBER_OF_DAYS = 9
		const val TEMP_NUMBER_OF_DAYS = 5
		const val BIG_AGU_LIMIT_LOAD_VOLUME = 0.6
		const val LOAD_REMOVAL_MARGIN = 5
	}
}