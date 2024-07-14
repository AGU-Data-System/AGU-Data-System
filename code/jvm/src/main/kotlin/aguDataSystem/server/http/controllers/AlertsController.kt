package aguDataSystem.server.http.controllers

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.models.output.alerts.AlertDetailsOutputModel
import aguDataSystem.server.http.controllers.models.output.alerts.GetAlertsOutputModel
import aguDataSystem.server.service.alerts.AlertsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for the alerts' endpoint.
 * TODO review and complete the documentation
 */
@RestController("Alerts")
@RequestMapping(URIs.Alerts.ROOT)
class AlertsController(private val service: AlertsService) {

	/**
	 * Gets all alerts.
	 *
	 * @return The alerts.
	 */
	@GetMapping
	fun getAlerts(): ResponseEntity<*> {
		return ResponseEntity.ok(GetAlertsOutputModel(service.getAlerts()))
	}

	/**
	 * Gets an alert by its id.
	 *
	 * @param alertId The alert id.
	 * @return The alert.
	 */
	@GetMapping(URIs.Alerts.BY_ID)
	fun getAlertById(@PathVariable alertId: Int): ResponseEntity<*> {
		return ResponseEntity.ok(AlertDetailsOutputModel(service.getAlertById(alertId)))
	}

	/**
	 * Updates the status of an alert.
	 *
	 * @param alertId The alert id.
	 * @return The updated alert.
	 */
	@PutMapping(URIs.Alerts.BY_ID)
	fun updateAlertStatus(@PathVariable alertId: Int): ResponseEntity<*> {
		return ResponseEntity.ok(GetAlertsOutputModel(service.updateAlertStatus(alertId)))
	}
}
