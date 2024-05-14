package aguDataSystem.server.service.errors.agu

/**
 * Error for updating an AGU
 *
 * @property AGUNotFound The AGU was not found.
 * @property InvalidCUI The CUI is invalid.
 * @property InvalidDNO The DNO is invalid.
 * @property InvalidCoordinates The coordinates are invalid.
 * @property InvalidMinLevel The minimum level is invalid.
 * @property InvalidMaxLevel The maximum level is invalid.
 * @property InvalidCriticalLevel The critical level is invalid.
 * @property InvalidLevels The levels are invalid.
 * @property InvalidLoadVolume The load volume is invalid.
 * @property InvalidContact The contact is invalid.
 * @property InvalidContactType The contact type is invalid.
 * @property InvalidTank The tank is invalid.
 * @property ProviderError There was an error with the provider.
 */
sealed class UpdateAGUError {
    data object AGUNotFound : UpdateAGUError()
    data object InvalidCUI : UpdateAGUError()
    data object InvalidDNO : UpdateAGUError()
    data object InvalidCoordinates : UpdateAGUError()
    data object InvalidMinLevel : UpdateAGUError()
    data object InvalidMaxLevel : UpdateAGUError()
    data object InvalidCriticalLevel : UpdateAGUError()
    data object InvalidLevels : UpdateAGUError()
    data object InvalidLoadVolume : UpdateAGUError()
    data object InvalidContact : UpdateAGUError()
    data object InvalidContactType : UpdateAGUError()
    data object InvalidTank : UpdateAGUError()
    data object ProviderError : UpdateAGUError()
}