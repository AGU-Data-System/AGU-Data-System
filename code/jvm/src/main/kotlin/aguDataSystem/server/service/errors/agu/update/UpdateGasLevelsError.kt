package aguDataSystem.server.service.errors.agu.update

import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.service.errors.agu.update.UpdateGasLevelsError.AGUNotFound
import aguDataSystem.server.service.errors.agu.update.UpdateGasLevelsError.InvalidLevels

/**
 * Represents the possible errors that can occur when updating the [GasLevels]
 *
 * @property AGUNotFound The AGU was not found
 * @property InvalidLevels The levels are invalid
 */
sealed class UpdateGasLevelsError {
	data object AGUNotFound : UpdateGasLevelsError()
	data object InvalidLevels : UpdateGasLevelsError()
}