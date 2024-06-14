package aguDataSystem.server.http.models.response.provider

import kotlinx.serialization.Serializable

/**
 * Represents a response for a gas provider
 *
 * @property id the id of the provider
 * @property measures the list of gas measures
 * @property lastFetch the last fetch date
 */
@Serializable
data class GasProviderResponse(
	val id: Int,
	val measures: GasMeasuresListResponse,
	val lastFetch: String
)