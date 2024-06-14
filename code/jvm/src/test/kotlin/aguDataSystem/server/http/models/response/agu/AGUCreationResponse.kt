package aguDataSystem.server.http.models.response.agu

import kotlinx.serialization.Serializable

/**
 * Response model for the creation of an AGU
 *
 * @property cui the CUI of the AGU
 */
@Serializable
data class AGUCreationResponse(
	val cui: String
)