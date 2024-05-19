package aguDataSystem.server.service.errors.agu

sealed class AddTankError {
    object AGUNotFound : AddTankError()
    object InvalidLevels : AddTankError()
}