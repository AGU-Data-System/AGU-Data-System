package aguDataSystem.server.service.errors.contact

import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.service.errors.contact.DeleteContactError.AGUNotFound

/**
 * Represents the possible errors that can occur when deleting a [Contact]
 *
 * @property AGUNotFound The AGU was not found
 */
sealed class DeleteContactError {
	data object AGUNotFound : DeleteContactError()
}