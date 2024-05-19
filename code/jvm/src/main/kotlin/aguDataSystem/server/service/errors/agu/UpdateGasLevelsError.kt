package aguDataSystem.server.service.errors.agu

sealed class UpdateGasLevelsError {
    object AGUNotFound : UpdateGasLevelsError()
    object InvalidLevels : UpdateGasLevelsError()
}