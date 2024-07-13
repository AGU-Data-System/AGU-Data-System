package aguDataSystem.server.http.controllers.models.output.alerts

import aguDataSystem.server.domain.alerts.Alert

/**
 * Output model for GetAlerts
 *
 * @param alerts The list of alerts
 * @param size The size of the list
 */
data class GetAlertsOutputModel(
	val alerts: List<Alert>,
	val size: Int
) {
	constructor(alerts: List<Alert>) : this(
		alerts = alerts,
		size = alerts.size
	)
}