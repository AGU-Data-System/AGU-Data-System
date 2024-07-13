package aguDataSystem.server.repository.alerts

import aguDataSystem.server.domain.alerts.Alert
import aguDataSystem.server.domain.alerts.AlertCreationInfo

/**
 * Repository for [Alert]s
 */
interface AlertsRepository {
	/**
	 * Returns a list of all [Alert]s
	 *
	 * @return A list of [Alert]s
	 */
	fun getAlerts(): List<Alert>

	/**
	 * Returns an [Alert] by its id
	 *
	 * @param id The id of the [Alert]
	 * @return The [Alert] with the given id
	 */
	fun getAlertById(id: Int): Alert

	/**
	 * Creates a new [Alert]
	 *
	 * @param alert The [AlertCreationInfo] for the new [Alert]
	 * @return The id of the new [Alert]
	 */
	fun createAlert(alert: AlertCreationInfo): Int

	/**
	 * Updates the status of an [Alert]
	 *
	 * @param id The id of the [Alert] to update
	 * @return A list of all [Alert]s
	 */
	fun updateAlertStatus(id: Int): List<Alert>
}