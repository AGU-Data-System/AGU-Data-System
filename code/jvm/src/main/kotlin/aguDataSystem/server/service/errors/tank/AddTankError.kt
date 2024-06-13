package aguDataSystem.server.service.errors.tank

import aguDataSystem.server.domain.tank.Tank
import aguDataSystem.server.service.errors.tank.AddTankError.AGUNotFound
import aguDataSystem.server.service.errors.tank.AddTankError.InvalidCapacity
import aguDataSystem.server.service.errors.tank.AddTankError.InvalidLevels
import aguDataSystem.server.service.errors.tank.AddTankError.InvalidLoadVolume
import aguDataSystem.server.service.errors.tank.AddTankError.InvalidTankNumber
import aguDataSystem.server.service.errors.tank.AddTankError.TankAlreadyExists

/**
 * Represents the possible errors that can occur when adding a [Tank]
 *
 * @property AGUNotFound The AGU was not found
 * @property InvalidLevels The levels are invalid
 * @property TankAlreadyExists The tank already exists
 * @property InvalidLoadVolume The load volume is invalid
 * @property InvalidCapacity The capacity is invalid
 * @property InvalidTankNumber The tank number is invalid
 */
sealed class AddTankError {
	data object AGUNotFound : AddTankError()
	data object InvalidLevels : AddTankError()
	data object TankAlreadyExists : AddTankError()
	data object InvalidLoadVolume : AddTankError()
	data object InvalidCapacity : AddTankError()
	data object InvalidTankNumber : AddTankError()
}