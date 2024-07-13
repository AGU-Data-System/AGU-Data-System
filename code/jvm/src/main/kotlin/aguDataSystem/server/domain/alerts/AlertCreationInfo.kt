package aguDataSystem.server.domain.alerts

/**
 * Represents an info model for creating a new [Alert]
 *
 * @property aguId The id of the AGU that the Alert is associated with
 * @property title The title of the Alert
 * @property message The message of the Alert
 */
data class AlertCreationInfo(
	val aguId: String,
	val title: String,
	val message: String,
)