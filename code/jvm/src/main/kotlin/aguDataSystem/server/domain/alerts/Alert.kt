package aguDataSystem.server.domain.alerts

import java.time.Instant

/**
 * Represents an Alert in the system.
 *
 * @property id the id of the Alert
 * @property agu the AGU that the Alert is associated with
 * @property timestamp the timestamp of the Alert
 * @property title the title of the Alert
 * @property message the message of the Alert
 * @property isResolved whether the Alert is resolved
 */
data class Alert(
	val id: Int,
	val agu: String,
	val timestamp: Instant,
	val title: String,
	val message: String,
	val isResolved: Boolean
)