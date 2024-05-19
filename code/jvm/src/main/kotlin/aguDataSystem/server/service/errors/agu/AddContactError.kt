package aguDataSystem.server.service.errors.agu

sealed class AddContactError {
    object AGUNotFound : AddContactError()
    object InvalidContact : AddContactError()
    object InvalidContactType : AddContactError()
    object ContactAlreadyExists : AddContactError()
}