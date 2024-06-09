package aguDataSystem.server.http.models.agu

import kotlinx.serialization.Serializable

/**
 * Request model for updating favourite AGU
 *
 * @param favourite: Boolean
 */
@Serializable
data class UpdateFavouriteAGURequestModel(
	val favourite: Boolean
)