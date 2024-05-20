package aguDataSystem.server.service.errors.agu.update

import aguDataSystem.server.domain.gasLevels.GasLevels

/**
 * Represents the possible errors that can occur when updating the [GasLevels].
 */
sealed class UpdateGasLevelsError {
	data object AGUNotFound : UpdateGasLevelsError()
	data object InvalidLevels : UpdateGasLevelsError()
}