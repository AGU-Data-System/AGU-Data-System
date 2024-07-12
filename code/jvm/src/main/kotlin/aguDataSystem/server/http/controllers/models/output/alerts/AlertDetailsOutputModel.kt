package aguDataSystem.server.http.controllers.models.output.alerts

import aguDataSystem.server.domain.alerts.Alert
import java.time.Instant

data class AlertDetailsOutputModel (
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
