package aguDataSystem.server.service.alerts

import aguDataSystem.server.domain.alerts.Alert
import aguDataSystem.server.domain.alerts.AlertCreationDTO
import aguDataSystem.server.repository.TransactionManager
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for managing [Alert]s
 *
 * @property transactionManager The [TransactionManager] for the service
 */
@Service
class AlertsService(
	private val transactionManager: TransactionManager,
) {
	/**
	 * Returns a list of all [Alert]s
	 *
	 * @return A list of [Alert]s
	 */
	fun getAlerts(): List<Alert> {
		return transactionManager.run {
			logger.info("Getting all alerts")
			val alerts = it.alertsRepository.getAlerts()
			logger.info("Retrieved {} alerts", alerts.size)

			return@run alerts
		}
	}

	/**
	 * Returns an [Alert] by its id
	 *
	 * @param id The id of the [Alert]
	 * @return The [Alert] with the given id
	 */
	fun getAlertById(id: Int): Alert {
		return transactionManager.run {
			logger.info("Getting alert with id: {}", id)
			val alert = it.alertsRepository.getAlertById(id)
			logger.info("Retrieved alert with id: {}", id)

			return@run alert
		}
	}

	/**
	 * Creates a new [Alert]
	 *
	 * @param alert The [AlertCreationDTO] for the new [Alert]
	 * @return The id of the new [Alert]
	 */
	fun createAlert(alert: AlertCreationDTO): Int {
		return transactionManager.run {
			logger.info("Creating alert")
			val alertId = it.alertsRepository.createAlert(alert.toAlertCreationInfo())
			logger.info("Created alert with id: {}", alertId)

			return@run alertId
		}
	}

	/**
	 * Updates the status of an [Alert]
	 *
	 * @param id The id of the [Alert] to update
	 * @return A list of all [Alert]s
	 */
	fun updateAlertStatus(id: Int): List<Alert> {
		return transactionManager.run {
			logger.info("Updating alert status with id: {}", id)
			val alerts = it.alertsRepository.updateAlertStatus(id)
			logger.info("Updated alert status with id: {}", id)

			return@run alerts
		}
	}

	companion object {
		private val logger = LoggerFactory.getLogger(AlertsService::class.java)
	}
}