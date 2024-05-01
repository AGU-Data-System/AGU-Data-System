package aguDataSystem.server.service.errors.agu

sealed class GetAGUError {
	data object AGUNotFound : GetAGUError()
}