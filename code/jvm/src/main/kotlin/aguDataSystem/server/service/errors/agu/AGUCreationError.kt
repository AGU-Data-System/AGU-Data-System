package aguDataSystem.server.service.errors.agu

/**
 * Error for creating an AGU
 */
sealed class AGUCreationError {
	//TODO: Insert Possible Errors
	data object InvalidCUI : AGUCreationError()
	data object InvalidDNO : AGUCreationError()
	data object InvalidCoordinates : AGUCreationError()
	data object InvalidMinLevel : AGUCreationError()
	data object InvalidMaxLevel : AGUCreationError()
	data object InvalidCriticalLevel : AGUCreationError()
	data object InvalidLevels : AGUCreationError()
	data object InvalidLoadVolume : AGUCreationError()
	data object InvalidContact : AGUCreationError()
	data object InvalidContactType : AGUCreationError()
	data object InvalidTank : AGUCreationError()
}