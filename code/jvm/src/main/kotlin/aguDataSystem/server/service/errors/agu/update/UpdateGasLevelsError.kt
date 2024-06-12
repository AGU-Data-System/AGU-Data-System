package aguDataSystem.server.service.errors.agu.update

import aguDataSystem.server.domain.gasLevels.GasLevels

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