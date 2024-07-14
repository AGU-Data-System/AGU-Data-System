package aguDataSystem.server.service.prediction.models

import kotlinx.serialization.Serializable

/**
 * Data class for the prediction response.
 *
 * @property date The date
 * @property consumption The consumption
 */
@Serializable
data class PredictionResponseModel(
	val date: String,
	val consumption: Double
)