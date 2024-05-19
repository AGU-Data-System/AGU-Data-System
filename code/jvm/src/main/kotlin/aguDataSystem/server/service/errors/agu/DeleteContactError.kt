package aguDataSystem.server.service.errors.agu

sealed class DeleteContactError {
    object AGUNotFound : DeleteContactError()
}