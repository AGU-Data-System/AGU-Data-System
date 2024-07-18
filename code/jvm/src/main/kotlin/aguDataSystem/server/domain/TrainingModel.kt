package aguDataSystem.server.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class for the training model.
 *
 * @property r2Score The R2 score of the training.
 * @property coefficients The coefficients of the training.
 * @property intercept The intercept of the training.
 */
@Serializable
data class TrainingModel(
    @SerialName("R^2 Score") val r2Score: Double,
    val coefficients: List<Double>,
    val intercept: Double
)