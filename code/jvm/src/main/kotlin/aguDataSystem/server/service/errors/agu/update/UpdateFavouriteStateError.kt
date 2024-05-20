package aguDataSystem.server.service.errors.agu.update

import aguDataSystem.server.domain.agu.AGU

/**
 * Represents the possible errors that can occur when updating the favorite state of an [AGU].
 */
sealed class UpdateFavouriteStateError {
	data object AGUNotFound : UpdateFavouriteStateError()
}