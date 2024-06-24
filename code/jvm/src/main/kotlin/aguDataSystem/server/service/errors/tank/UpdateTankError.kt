package aguDataSystem.server.service.errors.tank

/**
 * Represents the possible errors that can occur when updating a tank
 *
 * @property AGUNotFound The AGU was not found
 * @property InvalidLevels The levels are invalid
 * @property TankNotFound The tank was not found
 * @property InvalidCUI The CUI is invalid
 * @property InvalidCapacity The capacity is invalid
 * @property InvalidTankNumber The tank number is invalid
 */
sealed class UpdateTankError {
	data object AGUNotFound : UpdateTankError()
	data object InvalidLevels : UpdateTankError()
	data object TankNotFound : UpdateTankError()
	data object InvalidCUI : UpdateTankError()
	data object InvalidCapacity : UpdateTankError()
	data object InvalidTankNumber : UpdateTankError()
}