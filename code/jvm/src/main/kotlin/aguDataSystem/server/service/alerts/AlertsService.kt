package aguDataSystem.server.service.alerts

import aguDataSystem.server.domain.alerts.Alert
import aguDataSystem.server.domain.alerts.AlertCreationDTO
import aguDataSystem.server.repository.TransactionManager
import org.springframework.stereotype.Service

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
            it.alertsRepository.getAlerts()
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
            it.alertsRepository.getAlertById(id)
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
            it.alertsRepository.createAlert(alert.toAlertCreationInfo())
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
            it.alertsRepository.updateAlertStatus(id)
        }
    }
}