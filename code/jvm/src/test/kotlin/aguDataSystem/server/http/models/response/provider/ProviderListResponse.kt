package aguDataSystem.server.http.models.response.provider

import kotlinx.serialization.Serializable

/**
 * Response for a list of providers
 *
 * @property gasProviders the list of gas providers
 * @property temperatureProviders the list of temperature providers
 * @property size the size of the list
 */
@Serializable
data class ProviderListResponse(
	val gasProviders: List<GasProviderResponse>,
	val temperatureProviders: List<TemperatureProviderResponse>,
	val size: Int
)