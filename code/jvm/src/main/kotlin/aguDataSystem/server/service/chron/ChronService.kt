package aguDataSystem.server.service.chron

import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.prediction.PredictionService
import jakarta.annotation.PostConstruct
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalTime

/**
 * Service for managing the chron tasks,
 * for periodically fetching data from providers.
 *
 * @property transactionManager The transaction manager
 * @property fetchService The fetch service
 */
@Service
class ChronService(
	private val transactionManager: TransactionManager,
	private val fetchService: FetchService,
	private val predictionService: PredictionService
) {

	private val chronPoolSize = 10
	private val scheduledChron: MutableMap<Int, ScheduledFuture<*>> = ConcurrentHashMap()
	private val chronScheduler: ScheduledExecutorService =
		Executors.newScheduledThreadPool(chronPoolSize)

	/**
	 * Initializes the chron service.
	 */
	@PostConstruct
	fun initialize() {
		logger.info("Initializing Chron Service")
		scheduleChron()
		schedulePredictionAndLoadChronTask()
		logger.info("Chron Service initialized")
	}

	/**
	 * Schedules the chron tasks for the providers.
	 */
	fun scheduleChron() {
		val providers = transactionManager.run {
			logger.info("Fetching all providers")
			it.providerRepository.getAllProviders().also { providerList ->
				logger.info("Fetched {} providers", providerList.size)
			}
		}
		logger.info("Scheduling chron tasks based on providers")
		providers.forEach { provider ->
			val providerType = provider.getProviderType()
			val pollingFrequency = providerType.pollingFrequency

			logger.info("Scheduling chron task for provider: {}", provider)
			scheduleChronTask(provider, pollingFrequency)
		}
	}

	/**
	 * Schedules a chron task for a provider.
	 *
	 * @param provider The provider to schedule
	 */
	fun scheduleChronTask(provider: Provider, frequency: Duration) {
		val lastFetch = provider.lastFetch ?: LocalDateTime.MIN
		val delay = calculateInitialDelay(lastFetch, frequency)

		logger.info("Scheduling chron task for provider: {} with delay: {}", provider, delay)

		val future: ScheduledFuture<*> = chronScheduler.scheduleAtFixedRate({
			fetchService.fetchAndSave(provider, lastFetch)
		}, delay, frequency.toMillis(), TimeUnit.MILLISECONDS)
		scheduledChron[provider.id] = future

		logger.info("Scheduled chron task for provider: {}", provider)
	}

	/**
	 * Schedules the training chron task.
	 *
	 * Still sketchy, needs to be implemented
	 */
	//@Scheduled(cron = "0 0 0 * * SAT") // TODO: This should be a normal request with availability to train one or all AGUs
	fun scheduleTrainingChronTask(agu: AGUBasicInfo) {
		// TODO: needs to get the training frequency or be set with an annotation
		//  get the temperature for the past n days
		//  get the consumption for the past n days
		//  train the model with the prediction module
		//  save the model in the DB
		val nrOfDays = 10
		var temps: List<TemperatureMeasure> = emptyList()
		var consumptions: List<Double> = emptyList()
		transactionManager.run {
			logger.info("Fetching providers for AGU: {}", agu.cui)
			val aguProviders = it.providerRepository.getProviderByAGU(agu.cui)

			logger.info("Fetching temperature and gas measures for AGU: {}", agu.cui)
			aguProviders.forEach { provider ->
				when (provider.getProviderType()) {
					ProviderType.TEMPERATURE -> {
						temps = it.temperatureRepository.getTemperatureMeasures(provider.id, nrOfDays)
					}

					ProviderType.GAS -> {
						consumptions = it.gasRepository.getGasMeasures(provider.id, nrOfDays + 1, LocalTime.MIDNIGHT)
							.map { gasMeasure -> gasMeasure.level.toDouble() }
							.zipWithNext { a, b -> b - a } // calculate the consumption for each day
					}
				}
			}
		}

		logger.info("Generating training model for AGU: {}", agu.cui)
		val training = fetchService.generateTraining(temps, consumptions.map { consumption -> consumption.toInt() })

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
	 * Schedules the prediction chron task.
	 *
	 * This task will be responsible for predicting the consumption of each AGU as well as the needed loads according to the prediction levels.
	 * This task is scheduled to run every day at 08:30.
	 */
	fun schedulePredictionAndLoadChronTask() {
		chronScheduler.scheduleAtFixedRate({
			val allAGUs = transactionManager.run {
				it.aguRepository.getAGUsBasicInfo()
			}
			allAGUs.forEach { agu ->
				predictionService.processAGU(agu)
			}
		},
			Duration.between(LocalTime.now(), LocalTime.of(8, 30)).toMillis(), //TODO: Currently set to run at 08:30, could be configurable.
			Duration.ofDays(1).toMillis(),
			TimeUnit.MILLISECONDS
		)
	}

	/**
	 * Calculates the initial delay for a chron task.
	 *
	 * @param lastReading The last reading time
	 * @return The initial delay
	 */
	private fun calculateInitialDelay(lastReading: LocalDateTime, frequency: Duration): Long {
		val timeSinceLastReading = Duration.between(lastReading, LocalDateTime.now())
		return if (timeSinceLastReading >= frequency) 0 else timeSinceLastReading.toMillis()
	}

	companion object {
		private val logger = LoggerFactory.getLogger(ChronService::class.java)
	}
}