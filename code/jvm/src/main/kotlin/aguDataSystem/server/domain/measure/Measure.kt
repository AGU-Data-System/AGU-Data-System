package aguDataSystem.server.domain.measure

import java.time.LocalDateTime

/**
 * Represents a measure.
 *
 * @property timestamp The time when the reading was taken.
 * @property predictionFor The time for which the reading is a prediction, if it's the same as timestamp it's not a prediction.
 */
sealed class Measure {
	abstract val timestamp: LocalDateTime
	abstract val predictionFor: LocalDateTime
}
