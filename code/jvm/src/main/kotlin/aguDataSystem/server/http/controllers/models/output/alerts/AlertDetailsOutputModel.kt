package aguDataSystem.server.http.controllers.models.output.alerts

import aguDataSystem.server.domain.alerts.Alert
import java.time.Instant

/**
 * Output model for AlertDetails
 *
 * @param id The id of the alert
 * @param agu The AGU of the alert
 * @param timestamp The timestamp of the alert
 * @param title The title of the alert
 * @param message The message of the alert
 * @param isResolved The status of the alert
 */
data class AlertDetailsOutputModel(
	val id: Int,
	val agu: String,
	val timestamp: Instant,
	val title: String,
	val message: String,
	val isResolved: Boolean
) {
	constructor(alert: Alert) : this(
		id = alert.id,
		agu = alert.agu,
		timestamp = alert.timestamp,
		title = alert.title,
		message = alert.message,
		isResolved = alert.isResolved
	)
}
