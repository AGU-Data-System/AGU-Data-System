package aguDataSystem.server.domain

import java.time.LocalDateTime

/**
 * Represents a reading of a sensor.
 *
 * @property timestamp The time when the reading was taken.
 * @property predictionFor The time for which the reading is
 * a prediction if its null it's not a prediction.
 * @property data The data of the reading.
 */
data class Reading(
	val timestamp: LocalDateTime,
	val predictionFor: LocalDateTime,
	val data: Int
)
