package aguDataSystem.server.http.models.response.provider

import kotlinx.serialization.Serializable

/**
 * Represents a list of gas measures response
 *
 * @property gasMeasures the list of gas measures
 * @property size the size of the list
 */
@Serializable
data class GasMeasuresListResponse(
	val gasMeasures: List<GasMeasureResponse>,
	val size: Int
)