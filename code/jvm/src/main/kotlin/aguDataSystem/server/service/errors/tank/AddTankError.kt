package aguDataSystem.server.service.errors.tank

import aguDataSystem.server.domain.tank.Tank

/**
 * Represents the possible errors that can occur when adding a [Tank].
 */
sealed class AddTankError {
	data object AGUNotFound : AddTankError()
	data object InvalidLevels : AddTankError()
	data object TankAlreadyExists : AddTankError()
	data object InvalidLoadVolume : AddTankError()
	data object InvalidCapacity : AddTankError()
	data object InvalidTankNumber : AddTankError()
}