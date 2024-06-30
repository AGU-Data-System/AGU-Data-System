package aguDataSystem.server.service.prediction

import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.Transaction
import aguDataSystem.server.repository.TransactionManage
import aguDataSystem.server.service.chron.FetchService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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
     *
     */
    fun processAGU(agu: AGUBasicInfo) {
        logger.info("Fetching providers for AGU: {}", agu.cui)
        transactionManager.run { transaction ->
            val aguProviders = transaction.providerRepository.getProviderByAGU(agu.cui)
            val temperatureProviders = aguProviders.filter { it.getProviderType() == ProviderType.TEMPERATURE }
            val gasProviders = aguProviders.filter { it.getProviderType() == ProviderType.GAS }

            logger.info("Fetching temperature past and future, and gas measures for AGU: {}", agu.cui)
            val pastTemps = fetchPastTemperatures(transaction, temperatureProviders)
            val futureTemps = fetchFutureTemperatures(transaction, temperatureProviders)
            val consumptions = fetchGasConsumptions(transaction, gasProviders)

            val training = transaction.aguRepository.getTraining(agu.cui)
                ?: throw Exception("No training model found for AGU: ${agu.cui}")

            logger.info("Generating gas consumption predictions for AGU: {}", agu.cui)
            val predictions = fetchService.generatePredictions(
                pastTemps,
                futureTemps,
                consumptions.map { it.toInt() },
                training
            )

            if (predictions.isNotEmpty()) {
                savePredictions(transaction, agu, predictions)
                manageLoads(transaction, agu, predictions)
            } else {
                logger.error("Failed to generate gas consumption predictions for AGU: {}", agu.cui)
            }
        }

    }

    private fun fetchPastTemperatures(transaction: Transaction, providers: List<Provider>): List<TemperatureMeasure> {
        return providers.flatMap { transaction.temperatureRepository.getPredictionTemperatureMeasures(it.id, NUMBER_OF_DAYS) }
    }

    private fun fetchFutureTemperatures(transaction: Transaction, providers: List<Provider>): List<TemperatureMeasure> {
        return providers.flatMap { transaction.temperatureRepository.getTemperatureMeasures(it.id, NUMBER_OF_DAYS) }
    }

    private fun fetchGasConsumptions(transaction: Transaction, providers: List<Provider>): List<Double> {
        return providers.flatMap {
            transaction.gasRepository.getPredictionGasMeasures(it.id, NUMBER_OF_DAYS + 1, LocalTime.MIDNIGHT)
                .map { gasMeasure -> gasMeasure.level.toDouble() }
                .zipWithNext { a, b -> b - a }
        }
    }

    fun savePredictions(transaction: Transaction, agu: AGUBasicInfo, predictions: List<Double>) {
        logger.info("Saving gas consumption predictions for AGU: {}", agu.cui)
        val aguProvider = transaction.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
            ?: throw Exception("No gas provider found for AGU: ${agu.cui}")
        transaction.gasRepository.addGasMeasuresToProvider(aguProvider.id, predictions.mapIndexed { index, prediction ->
            GasMeasure(
                timestamp = LocalDateTime.now().plusDays(index.toLong()),
                predictionFor = LocalDateTime.now().plusDays(index.toLong()),
                level = prediction.toInt(),
                tankNumber = 1 // Adjust logic if needed
            )
        })
    }

    fun manageLoads(transaction: Transaction, agu: AGUBasicInfo, predictions: List<Double>) {
        val minLevel = agu.minLevel
        val currentLevel = getCurrentLevel(transaction, agu.cui)
        var predictedLevels = mutableListOf<Int>()

        predictions.forEachIndexed { index, prediction ->
            val date = LocalDate.now().plusDays(index.toLong())
            var predictedLevel = currentLevel - prediction + getLoadForDay(transaction, agu.cui, date)

            if (predictedLevel < minLevel) {
                scheduleLoad(transaction, agu.cui, date)
                predictedLevel += getLoadVolume(transaction, agu.cui)
            }

            predictedLevels.add(predictedLevel)
        }
    }

    fun getCurrentLevel(transaction: Transaction, aguCui: String): Int {
        // Fetch current level from the database or other source
        return transaction.gasRepository.getCurrentLevel(aguCui)
    }

    fun getLoadForDay(transaction: Transaction, aguCui: String, date: LocalDate): Int {
        // Fetch any scheduled load for the day
        return transaction.loadRepository.getLoadForDay(aguCui, date)?.loadVolume ?: 0
    }

    fun scheduleLoad(transaction: Transaction, aguCui: String, date: LocalDate) {
        transaction.loadRepository.scheduleLoad(ScheduledLoad(
            id = generateLoadId(transaction),
            aguCui = aguCui,
            date = date,
            timeOfDay = TimeOfDay.MORNING, // or appropriate time
            isManual = false,
            isConfirmed = true
        ))
    }

    fun getLoadVolume(transaction: Transaction, aguCui: String): Int {
        // Fetch the default load volume for an AGU
        return transaction.aguRepository.getDefaultLoadVolume(aguCui)
    }

    fun savePredictions(agu: AGUBasicInfo, predictions: List<Int>) {
        transactionManager.run {
            val aguProvider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
                ?: throw Exception("No gas provider found for AGU: ${agu.cui}")
            it.gasRepository.addGasMeasuresToProvider(aguProvider.id, predictions.mapIndexed { index, prediction ->
                GasMeasure(
                    timestamp = LocalDateTime.now().plusDays(index.toLong()),
                    predictionFor = LocalDateTime.now().plusDays(index.toLong()),
                    level = prediction.toInt(),
                    tankNumber = 1 // Adjust logic if needed
                )
            })
        }
    }
    // needs to be picked the polling frequency
    fun schedulePredictionChronTask(agu: AGUBasicInfo) {
        // TODO: needs to get the prediction frequency or be set with an annotation
        //  send the training model to the prediction module
        //  get the prediction for the next n days
        //  save the prediction in the DB
        val nrOfDays = 10
        var pastTemps: List<TemperatureMeasure> = emptyList()
        var futureTemps: List<TemperatureMeasure> = emptyList()
        var consumptions: List<Double> = emptyList()
        var training = ""
        transactionManager.run {
            logger.info("Fetching providers for AGU: {}", agu.cui)
            val aguProviders = it.providerRepository.getProviderByAGU(agu.cui)

            logger.info("Fetching temperature past and future, and gas measures for AGU: {}", agu.cui)
            aguProviders.forEach { provider ->
                when (provider.getProviderType()) {
                    ProviderType.TEMPERATURE -> {
                        pastTemps = it.temperatureRepository.getPredictionTemperatureMeasures(provider.id, nrOfDays)
                        futureTemps = it.temperatureRepository.getTemperatureMeasures(provider.id, nrOfDays)
                    }

                    ProviderType.GAS -> {
                        consumptions =
                            it.gasRepository.getPredictionGasMeasures(provider.id, nrOfDays + 1, LocalTime.MIDNIGHT)
                                .map { gasMeasure -> gasMeasure.level.toDouble() }
                                .zipWithNext { a, b -> b - a } // calculate the consumption for each day
                    }
                }
            }
            // TODO should i throw an exception here?
            training =
                it.aguRepository.getTraining(agu.cui) ?: throw Exception("No training model found for AGU: ${agu.cui}")
        }

        logger.info("Generating gas consumption predictions for AGU: {}", agu.cui)
        val predictions = fetchService.generatePredictions(
            pastTemps,
            futureTemps,
            consumptions.map { consumption -> consumption.toInt() },
            training
        )

        if (predictions.isNotEmpty()) {
            transactionManager.run {
                logger.info("Saving gas consumption predictions for AGU: {}", agu.cui)
                // TODO should i throw an exception here?
                val aguProvider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
                    ?: throw Exception("No gas provider found for AGU: ${agu.cui}")
                it.gasRepository.addGasMeasuresToProvider(aguProvider.id, predictions.mapIndexed { index, prediction ->
                    GasMeasure(
                        timestamp = LocalDateTime.now().plusDays(index.toLong()),
                        predictionFor = LocalDateTime.now().plusDays(index.toLong()),
                        level = prediction.toInt(),
                        tankNumber = 1 // TODO: how to use the tank number?
                    )
                })
            }
        } else {
            logger.error("Failed to generate gas consumption predictions for AGU: {}", agu.cui)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FetchService::class.java)
        const val NUMBER_OF_DAYS = 10
    }
}