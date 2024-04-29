package aguDataSystem.server.service

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

	private val defaultFrequency = Duration.ofMinutes(60)

	@PostConstruct
	fun initialize() {
		logger.info("Initializing Chron Service")
		scheduleActiveChron()
		logger.info("Chron Service initialized")
	}

	/**
	 * Schedules the chron tasks for the providers.
	 */
	fun scheduleActiveChron() {
		transactionManager.run {
			logger.info("Scheduling active chron tasks")
			val activeChron = it.providerRepository.getAllProviders()
			activeChron.forEach { provider ->
				logger.info("Scheduling chron task for provider: {}", provider)
				if (provider.getLatestReading().timestamp.plusHours(1) < LocalDateTime.now()) {
					scheduleChronTask(provider)
				}
			}
		}
	}

	/**
	 * Schedules a chron task for a provider.
	 *
	 * @param provider The provider to schedule
	 */
	fun scheduleChronTask(provider: Provider) {
		val delay = calculateInitialDelay(provider.getLatestReading().timestamp)

		logger.info("Scheduling chron task for provider: {} with delay: {}", provider, delay)

		val future: ScheduledFuture<*> = chronScheduler.scheduleAtFixedRate({
			fetchService.fetch(provider.id)
		}, delay, defaultFrequency.toMillis(), TimeUnit.MILLISECONDS)
		scheduledChron[provider.id] = future

		logger.info("Scheduled chron task for provider: {}", provider)
	}

	/**
	 * Calculates the initial delay for a chron task.
	 *
	 * @param lastReading The last reading time
	 * @return The initial delay
	 */
	private fun calculateInitialDelay(lastReading: LocalDateTime): Long {
		val timeSinceLastReading = Duration.between(lastReading, LocalDateTime.now())
		return if (timeSinceLastReading <= Duration.ofHours(1)) 0 else timeSinceLastReading.toMillis()
	}

	companion object {
		private val logger = LoggerFactory.getLogger(ChronService::class.java)
	}
}