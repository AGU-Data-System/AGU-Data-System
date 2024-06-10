package aguDataSystem.server.service.errors.agu

/**
 * Error for getting an AGU
 * TODO needs completion
 * @property AGUNotFound The AGU was not found.
 */
sealed class GetAGUError {
	data object AGUNotFound : GetAGUError()
	data object InvalidCUI : GetAGUError()
}