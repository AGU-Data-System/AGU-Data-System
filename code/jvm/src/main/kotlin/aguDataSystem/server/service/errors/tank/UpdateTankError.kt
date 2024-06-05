package aguDataSystem.server.service.errors.tank

/**
 * Represents the possible errors that can occur when updating a tank.
 */
sealed class UpdateTankError {
	data object AGUNotFound : UpdateTankError()
	data object InvalidLevels : UpdateTankError()
	data object TankNotFound : UpdateTankError()
	data object InvalidCUI : UpdateTankError()
	data object InvalidLoadVolume : UpdateTankError()
	data object InvalidCapacity : UpdateTankError()
	data object InvalidTankNumber : UpdateTankError()
}