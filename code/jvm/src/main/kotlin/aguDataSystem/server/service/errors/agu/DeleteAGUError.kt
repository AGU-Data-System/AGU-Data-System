package aguDataSystem.server.service.errors.agu

/**
 * Error for deleting an AGU
 *
 * @property InvalidCUI the CUI is invalid
 */
sealed class DeleteAGUError {
	data object InvalidCUI : DeleteAGUError()
}