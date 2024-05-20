package aguDataSystem.server.service.errors.agu.update

import aguDataSystem.server.domain.agu.AGU

/**
 * Represents the possible errors that can occur when updating notes of an [AGU].
 */
sealed class UpdateNotesError {
	data object AGUNotFound : UpdateNotesError()
}