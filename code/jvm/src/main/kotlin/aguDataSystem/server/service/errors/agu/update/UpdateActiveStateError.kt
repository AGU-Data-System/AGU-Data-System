package aguDataSystem.server.service.errors.agu.update

import aguDataSystem.server.domain.agu.AGU

/**
 * Represents the possible errors that can occur when updating the active state of an [AGU].
 */
sealed class UpdateActiveStateError {
	data object AGUNotFound : UpdateActiveStateError()
}