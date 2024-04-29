package aguDataSystem.server.domain.reading

import java.time.LocalDateTime

/**
 * Represents a reading of a sensor.
 *
 * @property timestamp The time when the reading was taken.
 * @property predictionFor The time for which the reading is
 * a prediction if its null it's not a prediction.
 */
sealed class Reading {
	abstract val timestamp: LocalDateTime
	abstract val predictionFor: LocalDateTime
}
