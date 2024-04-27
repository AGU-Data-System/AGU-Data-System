package aguDataSystem.server.domain

import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 * Represents a reading
 *
 * @property timeStamp The time when the reading was taken.
 * @property predictionFor The time for which the reading is
 * a prediction if its null it's not a prediction.
 * @property data The data of the reading.
 */
data class Reading(
	val timeStamp : LocalDateTime,
	val predictionFor : ZonedDateTime?,
	val data : String // maybe change to json
)