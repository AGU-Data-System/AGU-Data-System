package aguDataSystem.server.service.errors.contact

import aguDataSystem.server.domain.contact.Contact

/**
 * Represents the possible errors that can occur when deleting a [Contact]
 * TODO Needs completion
 */
sealed class DeleteContactError {
	data object AGUNotFound : DeleteContactError()
}