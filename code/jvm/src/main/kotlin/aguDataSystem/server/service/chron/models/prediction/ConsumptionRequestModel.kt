package aguDataSystem.server.service.chron.models.prediction

import java.time.LocalDate
import kotlinx.serialization.Serializable

/**
 * Data class for the consumption prediction request.
 *
 * @property level The level of the prediction
 * @property timestamp The timestamp of the prediction
 */
@Serializable
data class ConsumptionRequestModel(
	val level: Int,
	val timestamp: String
) {
	constructor(level: Int, timestamp: LocalDate) : this(
		level = level,
		timestamp = timestamp.toString()
	)
}