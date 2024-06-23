package aguDataSystem.server.http.models.request.agu

import kotlinx.serialization.Serializable

/**
 * Represents the request model for updating the active status of an AGU
 *
 * @property isActive The new active status of the AGU
 */
@Serializable
data class UpdateActiveAGURequestModel(
	val isActive: Boolean
)