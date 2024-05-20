package aguDataSystem.server.service.chron

import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.repository.TransactionManager
import jakarta.annotation.PostConstruct
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.Schedules
import org.springframework.stereotype.Service

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
	private val fetchService: FetchService
) {

	private val chronPoolSize = 10
	private val scheduledChron: MutableMap<Int, ScheduledFuture<*>> = ConcurrentHashMap()
	private val chronScheduler: ScheduledExecutorService =
		Executors.newScheduledThreadPool(chronPoolSize)

	@PostConstruct
	fun initialize() {
		logger.info("Initializing Chron Service")
		scheduleChron()
		logger.info("Chron Service initialized")
	}

	/**
	 * Schedules the chron tasks for the providers.
	 */
	//At every 10th minute from 2 through 59, Every Day at 09:01
	@Schedules(Scheduled(cron = "* 2/10 * * * *"), Scheduled(cron = "0 1 9 * * *"))
	fun scheduleChron() {
		transactionManager.run {
			logger.info("Scheduling chron tasks based on providers")
			val providerChron = it.providerRepository.getAllProviders()
			providerChron.forEach { provider ->
				val providerType = provider.getProviderType()
				val pollingFrequency = providerType.pollingFrequency.toMinutes()
				val lastFetch = provider.lastFetch

				if (lastFetch == null || lastFetch.plusMinutes(pollingFrequency) < LocalDateTime.now()) {
                    logger.info("Scheduling chron task for provider: {}", provider)
					scheduleChronTask(provider, providerType.pollingFrequency)
				}
			}
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