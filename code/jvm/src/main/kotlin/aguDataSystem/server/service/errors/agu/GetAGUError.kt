package aguDataSystem.server.service.errors.agu

/**
 * Error for getting an AGU
 *
 * @property AGUNotFound The AGU was not found.
 */
sealed class GetAGUError {
	data object AGUNotFound : GetAGUError()
}