package aguDataSystem.server.http.controllers.models.input.alerts

import aguDataSystem.server.domain.alerts.AlertCreationDTO

/**
 * The input model for creating an alert
 *
 * @param agu the AGU CUI of the alert
 * @param title the title of the alert
 * @param message the message of the alert
 */
data class AlertsCreationInputModel(
    val agu: String,
    val title: String,
    val message: String,
) {
    /**
     * Converts the input model to a data transfer object
     *
     * @receiver the alert creation input model
     * @return the alert creation data transfer object
     */
    fun toDTO() = AlertCreationDTO(
        aguId = this.agu,
        title = this.title,
        message = this.message,
    )
}