package aguDataSystem.server.service.errors.agu

sealed class UpdateFavouriteStateError {
    object AGUNotFound : UpdateFavouriteStateError()
}