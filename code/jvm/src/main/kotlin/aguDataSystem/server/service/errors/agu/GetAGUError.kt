package aguDataSystem.server.service.errors.agu

/**
 * Error for getting an AGU
 *
 * @property AGUNotFound The AGU was not found
 * @property InvalidCUI The CUI is invalid
 */
sealed class GetAGUError {
	data object AGUNotFound : GetAGUError()
	data object InvalidCUI : GetAGUError()
}