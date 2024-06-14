package aguDataSystem.server.http.models.response.provider

import kotlinx.serialization.Serializable

/**
 * Represents the response of a temperature provider
 *
 * @property id the id of the provider
 * @property measures the list of temperature measures
 * @property lastFetch the last fetch date
 */
@Serializable
data class TemperatureProviderResponse(
	val id: Int,
	val measures: TemperatureMeasureListResponse,
	val lastFetch: String
)