package aguDataSystem.server.service.errors.agu.update

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.service.errors.agu.update.UpdateNotesError.AGUNotFound

/**
 * Represents the possible errors that can occur when updating notes of an [AGU]
 *
 * @property AGUNotFound The AGU was not found
 */
sealed class UpdateNotesError {
	data object AGUNotFound : UpdateNotesError()
}