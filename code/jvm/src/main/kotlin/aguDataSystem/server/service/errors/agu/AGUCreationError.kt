package aguDataSystem.server.service.errors.agu

/**
 * Error for creating an AGU
 *
 * @property AGUAlreadyExists The AGU already exists
 * @property AGUNameAlreadyExists The AGU name already exists
 * @property InvalidCUI The CUI is invalid
 * @property InvalidEIC The EIC is invalid
 * @property InvalidName The name is invalid
 * @property DNONotFound The DNO was not found
 * @property InvalidCoordinates The coordinates are invalid
 * @property InvalidMinLevel The minimum level is invalid
 * @property InvalidMaxLevel The maximum level is invalid
 * @property InvalidCriticalLevel The critical level is invalid
 * @property InvalidLevels The levels are invalid
 * @property InvalidLoadVolume The load volume is invalid
 * @property InvalidContact The contact is invalid
 * @property InvalidContactType The contact type is invalid
 * @property InvalidTank The tank is invalid
 * @property ProviderError There was an error with the provider
 * @property TransportCompanyNotFound The transport company was not found
 */
sealed class AGUCreationError {
	data object AGUAlreadyExists : AGUCreationError()
	data object AGUNameAlreadyExists : AGUCreationError()
	data object InvalidCUI : AGUCreationError()
	data object InvalidEIC : AGUCreationError()
	data object InvalidName : AGUCreationError()
	data object DNONotFound : AGUCreationError()
	data object InvalidCoordinates : AGUCreationError()
	data object InvalidMinLevel : AGUCreationError()
	data object InvalidMaxLevel : AGUCreationError()
	data object InvalidCriticalLevel : AGUCreationError()
	data object InvalidLevels : AGUCreationError()
	data object InvalidLoadVolume : AGUCreationError()
	data object InvalidContact : AGUCreationError()
	data object InvalidContactType : AGUCreationError()
	data object InvalidTank : AGUCreationError()
	data object ProviderError : AGUCreationError()
	data object TransportCompanyNotFound : AGUCreationError()
}