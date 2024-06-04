package aguDataSystem.server.http.controllers.agu.models.input.agu

import aguDataSystem.server.domain.agu.AGU

/**
 * Represents the input model for updating the favourite status of an [AGU]
 *
 * @property isFavourite The new favourite status of the AGU
 */
data class UpdateFavouriteAGUInputModel(
	val isFavourite: Boolean
)
