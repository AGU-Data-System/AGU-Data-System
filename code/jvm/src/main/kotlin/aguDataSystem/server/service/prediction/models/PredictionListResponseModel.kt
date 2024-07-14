package aguDataSystem.server.service.prediction.models

import kotlinx.serialization.Serializable

/**
 * Data class for the prediction list response.
 *
 * @property predictionList The prediction list of the response
 */
@Serializable
data class PredictionListResponseModel(
	val predictionList: List<PredictionResponseModel>
)