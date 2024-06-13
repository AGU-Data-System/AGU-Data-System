package aguDataSystem.server.service.errors.agu.update

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.service.errors.agu.update.UpdateFavouriteStateError.AGUNotFound

/**
 * Represents the possible errors that can occur when updating the favorite state of an [AGU]
 *
 * @property AGUNotFound The AGU was not found
 */
sealed class UpdateFavouriteStateError {
	data object AGUNotFound : UpdateFavouriteStateError()
}