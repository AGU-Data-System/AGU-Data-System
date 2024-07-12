package aguDataSystem.server.http.controllers.models.output.alerts

import aguDataSystem.server.domain.alerts.Alert

data class GetAlertsOutputModel (
    val alerts: List<Alert>,
    val size: Int
) {
    constructor(alerts: List<Alert>) : this(
        alerts = alerts,
        size = alerts.size
    )
}