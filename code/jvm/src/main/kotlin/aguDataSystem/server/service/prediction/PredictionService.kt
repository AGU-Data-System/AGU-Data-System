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
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

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
            ).map { prediction -> PredictionResponseModel(prediction.date, abs(prediction.consumption)) }

            if (predictions.isNotEmpty()) {
                val completeAGU =
                    transaction.aguRepository.getAGUByCUI(agu.cui) ?: throw Exception("AGU not found: ${agu.cui}")

                val predictedLevels: List<Int>
                if (completeAGU.loadVolume < BIG_AGU_LIMIT_LOAD_VOLUME) {
                    predictedLevels = manageLoads(transaction, agu, predictions)
                } else {
                    logger.info("AGU {} is to small to have automatic scheduling of loads", agu.cui)

                    val tanks = transaction.tankRepository.getAGUTanks(agu.cui)

                    if (tanks.isEmpty()) {
                        logger.error("No tanks found for AGU: {}", agu.cui)
                    }

                    val currentLevel = getCombinedCurrentLevel(transaction, agu.cui, gasProvider.id)

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
        var levelList =
            transaction.gasRepository.getGasMeasures(providerId, NUMBER_OF_DAYS + 1, LocalTime.MIDNIGHT).toMutableList()

        if (levelList.size < NUMBER_OF_DAYS + 1) {
            val lastGasMeasure = levelList.lastOrNull()
            if (lastGasMeasure != null) {
                var lastLevel = lastGasMeasure.level
                var lastTimestamp = lastGasMeasure.timestamp
                var lastPredictionFor = lastGasMeasure.predictionFor

                levelList.removeAll(levelList)
                levelList.add(lastGasMeasure)
                for (i in 1..NUMBER_OF_DAYS) {
                    lastLevel = (lastLevel * Random.nextDouble(0.95, 1.0)).roundToInt()
                    lastTimestamp = lastTimestamp.minusDays(1)
                    lastPredictionFor = lastPredictionFor.minusDays(1)

                    levelList.add(
                        GasMeasure(
                            timestamp = lastTimestamp,
                            level = lastLevel,
                            predictionFor = lastPredictionFor,
                            tankNumber = lastGasMeasure.tankNumber,
                        )
                    )
                }
            } else {
                logger.error("No gas measures found for provider: {}", providerId)
                return emptyList()
            }
        }

        levelList = levelList.sortedBy { it.timestamp }.toMutableList()

        return levelList.map { gasMeasure -> gasMeasure.level.toDouble() } // reduce the error
            .zipWithNext { a, b -> b - a } // calculate the consumption for each day
            .mapIndexed { idx, consumption ->
                ConsumptionRequestModel(
                    consumption.roundToInt(), levelList[idx].timestamp.toLocalDate()
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
        val futureTemps =
            tm.temperatureRepository.getPredictionTemperatureMeasures(providerId, TEMP_NUMBER_OF_DAYS).map { temp ->
                TemperatureRequestModel(
                    min = temp.min, max = temp.max, timeStamp = temp.predictionFor.toLocalDate()
                )
            }.toMutableList()

        if (futureTemps.size < TEMP_NUMBER_OF_DAYS) {
            val lastTemp = futureTemps.lastOrNull() //The last
            if (lastTemp != null) {
                var daysToFill = TEMP_NUMBER_OF_DAYS - futureTemps.size
                var increment = 1L
                while (daysToFill > 0) {
                    futureTemps.add(
                        TemperatureRequestModel(
                            min = (lastTemp.min * Random.nextDouble(0.95, 1.05)).roundToInt(),
                            max = (lastTemp.max * Random.nextDouble(0.95, 1.05)).roundToInt(),
                            timeStamp = LocalDate.parse(lastTemp.timeStamp).plusDays(increment)
                        )
                    )
                    daysToFill--
                    increment++
                }
            } else {
                logger.error("No temperature predictions found for provider: {}", providerId)
                return emptyList()
            }
        }

        val previousTemps =
            tm.temperatureRepository.getPastTemperatureMeasures(providerId, NUMBER_OF_DAYS).mapIndexed { idx, temp ->
                TemperatureRequestModel(
                    min = temp.min, max = temp.max, timeStamp = temp.timestamp.toLocalDate()
                )
            }.toMutableList()

        if (previousTemps.size < NUMBER_OF_DAYS) {
            val lastTemp = previousTemps.firstOrNull() ?: futureTemps.firstOrNull()
            if (lastTemp != null) {
                var daysToFill = NUMBER_OF_DAYS - previousTemps.size
                var decrement = 1L
                while (daysToFill > 0) {
                    previousTemps.add(
                        TemperatureRequestModel(
                            min = (lastTemp.min * Random.nextDouble(0.95, 1.05)).roundToInt(),
                            max = (lastTemp.max * Random.nextDouble(0.95, 1.05)).roundToInt(),
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
            return previousTemps.sortedBy { it.timeStamp }
        }

        return (previousTemps + futureTemps).sortedBy { it.timeStamp }
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

        val currentLevel = getCombinedCurrentLevel(transaction, agu.cui, gasProvider.id)

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
                        title = "Scheduled Load removed for AGU: ${agu.name}",
                        message = "A load was removed because it was not needed"
                    )
                )
                totalLevel -= percentageAmountOfLoad
            } else if (totalLevel <= (minLevel + LOAD_REMOVAL_MARGIN)) {
                if (loadForDay != null) {
                    // Call in alert because even with a load in said day the level is below the minimum threshold
                    alertsService.createAlert(
                        AlertCreationDTO(
                            aguId = agu.cui, title = "Predicted Gas level for AGU: ${agu.name} ", message = "Predicted Gas level is below the minimum threshold"
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
                        predictionFor = LocalDateTime.of(LocalDate.now().plusDays(index.toLong()), END_OF_DAY),
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

    /**
     * Gets the combined current level of all tanks of an AGU
     *
     * @param transaction the transaction to use
     * @param cui the CUI of the AGU
     * @param providerId the ID of the gas provider
     *
     * @return the combined current level
     */
    private fun getCombinedCurrentLevel(transaction: Transaction, cui: String, providerId: Int): Int {

        val tanks = transaction.tankRepository.getAGUTanks(cui)

        if (tanks.isEmpty()) {
            logger.error("No tanks found for AGU: {}", cui)
            return 0
        }

        val tankLevels = transaction.gasRepository.getLatestMeasures(cui, providerId)

        val totalCapacity = tanks.sumOf { it.capacity }
        val weightedSum = tankLevels.sumOf { it.level * tanks.first { tank -> tank.number == it.tankNumber }.capacity }

        return weightedSum / totalCapacity
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FetchService::class.java)
        private val END_OF_DAY = LocalTime.of(23, 59, 59)
        const val NUMBER_OF_DAYS = 9
        const val TEMP_NUMBER_OF_DAYS = 5
        const val BIG_AGU_LIMIT_LOAD_VOLUME = 60
        const val LOAD_REMOVAL_MARGIN = 5
    }
}