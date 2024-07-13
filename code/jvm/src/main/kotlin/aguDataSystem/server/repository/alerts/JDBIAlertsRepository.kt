package aguDataSystem.server.repository.alerts

import aguDataSystem.server.domain.alerts.Alert
import aguDataSystem.server.domain.alerts.AlertCreationInfo
import aguDataSystem.server.repository.contact.JDBIContactRepository
import java.time.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [AlertsRepository]
 *
 * @property handle The JDBI handle
 */
class JDBIAlertsRepository(private val handle: Handle) : AlertsRepository {

	/**
	 * Get all alerts
	 *
	 * @return List of [Alert]
	 */
	override fun getAlerts(): List<Alert> {
		logger.info("Getting all alerts")

		val alerts = handle.createQuery(
			"""
                SELECT *
                FROM alerts
                WHERE is_resolved = false
            """.trimIndent()
		)
			.mapTo<Alert>().list()

		logger.info("Got all alerts")

		return alerts
	}

	/**
	 * Get alert by id
	 *
	 * @param id The id of the alert
	 * @return [Alert]
	 */
	override fun getAlertById(id: Int): Alert {
		logger.info("Getting alert with id {}", id)

		val alert = handle.createQuery(
			"""
                SELECT *
                FROM alerts
                WHERE id = :id
            """.trimIndent()
		)
			.bind("id", id)
			.mapTo<Alert>()
			.findOne()

		logger.info("Got alert with id {}", id)

		return alert.get()
	}

	/**
	 * Create an alert
	 *
	 * @param alert The [AlertCreationInfo] object
	 * @return The id of the created alert
	 */
	override fun createAlert(alert: AlertCreationInfo): Int {
		logger.info("Creating alert with title {}", alert.title)

		val alertId = handle.createUpdate(
			"""
                INSERT INTO alerts (agu_cui, timestamp, title, message, is_resolved)
                VALUES (:agu_cui, :timestamp ,:title, :message, :is_resolved)
            """.trimIndent()
		)
			.bind("agu_cui", alert.aguId)
			.bind("timestamp", Instant.now())
			.bind("title", alert.title)
			.bind("message", alert.message)
			.bind("is_resolved", false)
			.executeAndReturnGeneratedKeys(Alert::id.name)
			.mapTo<Int>()
			.one()

		logger.info("Created alert with title {}", alert.title)

		return alertId
	}

	/**
	 * Update alert status
	 *
	 * @param id The id of the alert
	 * @return List of [Alert]
	 */
	override fun updateAlertStatus(id: Int): List<Alert> {
		logger.info("Updating alert status with id {}", id)

		handle.createUpdate(
			"""
                UPDATE alerts
                SET is_resolved = true
                WHERE id = :id
            """.trimIndent()
		)
			.bind("id", id)
			.execute()

		logger.info("Updated alert status with id {}", id)

		return getAlerts()
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIContactRepository::class.java)
	}
}