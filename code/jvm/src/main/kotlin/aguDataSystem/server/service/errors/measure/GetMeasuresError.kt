package aguDataSystem.server.service.errors.measure

/**
 * Error for getting measures
 *
 * @property AGUNotFound The AGU was not found.
 * @property ProviderNotFound The provider was not found.
 */
sealed class GetMeasuresError {
	data object AGUNotFound : GetMeasuresError()
	data object ProviderNotFound : GetMeasuresError()
	data object InvalidDays : GetMeasuresError()
	data object InvalidTime : GetMeasuresError()
	data object InvalidCUI : GetMeasuresError()
}