package aguDataSystem.server.service.errors.contact

import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.service.errors.contact.AddContactError.AGUNotFound
import aguDataSystem.server.service.errors.contact.AddContactError.ContactAlreadyExists
import aguDataSystem.server.service.errors.contact.AddContactError.InvalidContact
import aguDataSystem.server.service.errors.contact.AddContactError.InvalidContactType

/**
 * Represents the possible errors that can occur when deleting a [Contact]
 *
 * @property AGUNotFound The AGU was not found
 * @property InvalidContact The contact is invalid
 * @property InvalidContactType The contact type is invalid
 * @property ContactAlreadyExists The contact already exists
 */
sealed class AddContactError {
	data object AGUNotFound : AddContactError()
	data object InvalidContact : AddContactError()
	data object InvalidContactType : AddContactError()
	data object ContactAlreadyExists : AddContactError()
}