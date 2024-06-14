package aguDataSystem.server.http.models.response.provider

import kotlinx.serialization.Serializable

/**
 * Gas measure response
 *
 * @param timestamp Timestamp of the measure
 * @param predictionFor Timestamp of the prediction for the measure
 * @param level Level of the measure
 * @param tankNumber Tank number of the measure
 */
@Serializable
data class GasMeasureResponse(
	val timestamp: String,
	val predictionFor: String?,
	val level: Int,
	val tankNumber: Int
)