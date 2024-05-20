package aguDataSystem.server.service.errors.contact

import aguDataSystem.server.domain.contact.Contact

/**
 * Represents the possible errors that can occur when deleting a [Contact].
 */
sealed class AddContactError {
	data object AGUNotFound : AddContactError()
	data object InvalidContact : AddContactError()
	data object InvalidContactType : AddContactError()
	data object ContactAlreadyExists : AddContactError()
}