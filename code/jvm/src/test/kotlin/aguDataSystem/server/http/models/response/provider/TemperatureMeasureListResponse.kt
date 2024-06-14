package aguDataSystem.server.http.models.response.provider

import kotlinx.serialization.Serializable

/**
 * Represents a list of temperature measures
 *
 * @property temperatureMeasures the list of temperature measures
 * @property size the size of the list
 */
@Serializable
data class TemperatureMeasureListResponse(
	val temperatureMeasures: List<TemperatureMeasureResponse>,
	val size: Int
)