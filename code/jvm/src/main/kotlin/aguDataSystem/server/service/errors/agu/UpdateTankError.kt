package aguDataSystem.server.service.errors.agu

sealed class UpdateTankError {
    object AGUNotFound : UpdateTankError()
    object InvalidLevels : UpdateTankError()
    object TankNotFound : UpdateTankError()
}