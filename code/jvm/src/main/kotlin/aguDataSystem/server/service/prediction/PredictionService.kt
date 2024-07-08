package aguDataSystem.server.service.prediction

import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.Transaction
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.chron.FetchService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class PredictionService(
    private val transactionManager: TransactionManager,
    private val fetchService: FetchService
) {

    /**
     * Receives an AGU and gets/stores the predictions for the next n days and calculate when loads are needed for this AGU
     *
     * @param agu The AGU to process
     */
    fun processAGU(agu: AGUBasicInfo) {

        transactionManager.run { transaction ->

            logger.info("Fetching providers for AGU: {}", agu.cui)
            val temperatureProvider = transaction.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.TEMPERATURE)
                ?: throw Exception("No temperature provider found for AGU: ${agu.cui}")
            val gasProvider = transaction.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
                ?: throw Exception("No gas provider found for AGU: ${agu.cui}") //TODO: Check for exceptions

            logger.info("Fetching temperature past and future, and gas consumptions for AGU: {}", agu.cui)
            val pastTemps = transaction.temperatureRepository.getPredictionTemperatureMeasures(temperatureProvider.id, NUMBER_OF_DAYS)
            val futureTemps = transaction.temperatureRepository.getTemperatureMeasures(temperatureProvider.id, NUMBER_OF_DAYS)
            val consumptions = fetchGasConsumptions(transaction, gasProvider.id)

            logger.info("Fetching training model for AGU: {}", agu.cui)
            val training = transaction.aguRepository.getTraining(agu.cui)
                ?: throw Exception("No training model found for AGU: ${agu.cui}")

            logger.info("Generating gas consumption predictions for AGU: {}", agu.cui)
            val predictions = fetchService.generatePredictions(
                pastTemps,
                futureTemps,
                consumptions,
                training
            )

            if (predictions.isNotEmpty()) {
                val predictedLevels = manageLoads(transaction, agu, predictions)
                savePredictions(transaction, agu, predictedLevels, gasProvider.id)
            } else {
                logger.error("Failed to generate gas consumption predictions for AGU: {}", agu.cui)
            }
        }

    }

    private fun fetchGasConsumptions(transaction: Transaction, providerId: Int): List<Int> {
        val gasMeasures = transaction.gasRepository.getGasMeasures(providerId, NUMBER_OF_DAYS + 1, LocalTime.MIDNIGHT)
        val dailyConsumptions = mutableMapOf<LocalDate, Int>()

        //Multiple tanks might be used, so we need to sum the levels that are from the same day
        gasMeasures.forEach { gasMeasure ->
            val date = gasMeasure.timestamp.toLocalDate()
            dailyConsumptions[date] = dailyConsumptions.getOrDefault(date, 0) + gasMeasure.level
        }

        return dailyConsumptions.values.toList().zipWithNext { a, b -> b - a }
    }

    private fun manageLoads(transaction: Transaction, agu: AGUBasicInfo, predictions: List<Int>): List<Int> {
        val fullAGU = transaction.aguRepository.getAGUByCUI(agu.cui) ?: throw Exception("AGU not found: ${agu.cui}")
        val minLevel = fullAGU.levels.min
        val currentLevel = transaction.gasRepository.getLatestLevels(agu.cui).sumOf { it.level }
        val predictedLevels = mutableListOf<Int>()

        var cumulativeConsumption = 0
        predictions.forEachIndexed { index, prediction ->
            val date = LocalDate.now().plusDays(index.toLong())
            cumulativeConsumption += prediction
            var totalLevel = currentLevel - cumulativeConsumption
            val loadForDay = getLoadAmountForDay(transaction, agu.cui, date) * fullAGU.loadVolume

            totalLevel += loadForDay.toInt()
            if (totalLevel < minLevel) {
                val adjustedDate = adjustForWeekend(date)
                transaction.loadRepository.scheduleLoad(
                    ScheduledLoadCreationDTO(
                        aguCui = agu.cui,
                        date = adjustedDate,
                        isManual = false
                    )
                )
                totalLevel += fullAGU.loadVolume
            }
            predictedLevels.add(totalLevel)
        }
        return predictedLevels
    }

    private fun savePredictions(transaction: Transaction, agu: AGUBasicInfo, predictedLevels: List<Int>, gasProviderId: Int) {
        val tanks = transaction.tankRepository.getAGUTanks(agu.cui)

        if (tanks.isEmpty()) {
            logger.error("No tanks found for AGU: {}", agu.cui)
            return
        }

        val totalCapacity = tanks.sumOf { it.capacity }

        predictedLevels.forEachIndexed { index, predictedLevel ->
            tanks.forEach { tank ->
                val tankLevel = (predictedLevel * tank.capacity / totalCapacity).toInt()
                transaction.gasRepository.addGasMeasuresToProvider(
                    gasProviderId,

                    GasMeasure(
                        timestamp = LocalDateTime.now().plusDays(index.toLong()),
                        predictionFor = LocalDateTime.now().plusDays(index.toLong()),
                        level = tankLevel,
                        tankNumber = tank.number
                    )
                )
            }
        }
    }

    /*private fun savePredictions(transaction: Transaction, agu: AGUBasicInfo, predictions: List<Int>, gasProviderId: Int) {
        transaction.gasRepository.addGasMeasuresToProvider(gasProviderId, predictions.mapIndexed { index, prediction ->
            val numberOfDay = index.toLong()
            GasMeasure(
                timestamp = LocalDateTime.now(),
                predictionFor = LocalDateTime.now().plusDays(numberOfDay),
                level = prediction,
                tankNumber = 1 //TODO: Verify we should save to the first tank
            )
        })
    }*/

    private fun getLoadAmountForDay(transaction: Transaction, aguCui: String, date: LocalDate): Double {
        return transaction.loadRepository.getLoadForDay(aguCui, date)?.amount ?: 0.0
    }

    private fun adjustForWeekend(date: LocalDate): LocalDate {
        return when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> date.minusDays(1)
            DayOfWeek.SUNDAY -> date.minusDays(2)
            else -> date
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FetchService::class.java)
        const val NUMBER_OF_DAYS = 10
    }
}