package aguDataSystem.server.service.chron.models.prediction

import java.time.LocalDate
import kotlinx.serialization.Serializable

/**
 * Data class for the temperature prediction request.
 *
 * @property max The maximum temperature
 * @property min The minimum temperature
 * @property timeStamp The timestamp of the prediction
 */
@Serializable
data class TemperatureRequestModel(
	val max: Int,
	val min: Int,
	val timeStamp: String
) {
	constructor(max: Int, min: Int, timeStamp: LocalDate) : this(
		max = max,
		min = min,
		timeStamp = timeStamp.toString()
	)
}