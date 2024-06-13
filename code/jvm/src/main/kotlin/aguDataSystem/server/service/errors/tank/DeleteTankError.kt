package aguDataSystem.server.service.errors.tank

/**
 * Error for deleting a tank
 *
 * @property InvalidCUI the CUI is invalid
 * @property AGUNotFound the AGU was not found
 */
sealed class DeleteTankError {
	data object InvalidCUI : DeleteTankError()
	data object AGUNotFound : DeleteTankError()
}