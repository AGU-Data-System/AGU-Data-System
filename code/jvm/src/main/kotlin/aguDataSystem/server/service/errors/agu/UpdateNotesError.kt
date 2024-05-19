package aguDataSystem.server.service.errors.agu

sealed class UpdateNotesError {
    object AGUNotFound : UpdateNotesError()
}