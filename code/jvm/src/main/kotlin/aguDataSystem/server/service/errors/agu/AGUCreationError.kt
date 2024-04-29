package aguDataSystem.server.service.errors.agu

/**
 * Error for creating an AGU
 */
sealed class AGUCreationError {
    //TODO: Insert Possible Errors
    object InvalidCUI : AGUCreationError()
    object InvalidDNO : AGUCreationError()
    object InvalidCoordinates : AGUCreationError()
    object InvalidMinLevel : AGUCreationError()
    object InvalidMaxLevel : AGUCreationError()
    object InvalidCriticalLevel : AGUCreationError()
    object InvalidLevels : AGUCreationError()
    object InvalidLoadVolume : AGUCreationError()
    object InvalidContact : AGUCreationError()
    object InvalidContactType : AGUCreationError()
    object InvalidTank : AGUCreationError()
}