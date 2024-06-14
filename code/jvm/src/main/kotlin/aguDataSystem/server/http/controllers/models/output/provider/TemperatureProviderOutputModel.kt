package aguDataSystem.server.http.controllers.models.output.provider

import aguDataSystem.server.domain.measure.toTemperatureMeasures
import aguDataSystem.server.domain.provider.Provider

/**
 * Output model for the temperature provider
 *
 * @property id The id of the provider
 * @property measures The measures of the provider
 * @property lastFetch The last fetch of the provider
 */
data class TemperatureProviderOutputModel(
	val id: Int,
	val measures: TemperatureMeasureListOutputModel,
	val lastFetch: String
) {
	constructor(provider: Provider) : this(
		id = provider.id,
		measures = TemperatureMeasureListOutputModel(provider.measures.toTemperatureMeasures()),
		lastFetch = provider.lastFetch.toString()
	)
}