package aguDataSystem.server.domain.alerts

/**
 * Represents a DTO model for creating a new [Alert]
 *
 * @property aguId The id of the AGU that the Alert is associated with
 * @property title The title of the Alert
 * @property message The message of the Alert
 */
data class AlertCreationDTO(
    val aguId: String,
    val title: String,
    val message: String,
) {
    fun toAlertCreationInfo(): AlertCreationInfo {
        return AlertCreationInfo(
            aguId = aguId,
            title = title,
            message = message
        )
    }
}