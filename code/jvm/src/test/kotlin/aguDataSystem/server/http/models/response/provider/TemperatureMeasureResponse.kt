package aguDataSystem.server.http.models.response.provider

import kotlinx.serialization.Serializable

/**
 * Response for a temperature measure
 *
 * @property timestamp the timestamp of the measure
 * @property predictionFor the prediction for the measure
 * @property min the minimum temperature
 * @property max the maximum temperature
 */
@Serializable
class TemperatureMeasureResponse(
	val timestamp: String,
	val predictionFor: String?,
	val min: Int,
	val max: Int,
)