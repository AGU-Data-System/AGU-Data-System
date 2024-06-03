package aguDataSystem.server.http.controllers.agu.models.output.provider

import aguDataSystem.server.domain.measure.toGasMeasures
import aguDataSystem.server.domain.measure.toTemperatureMeasures
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType

/**
 * Output model for Provider
 *
 * @property id The id of the provider
 * @property measures The measures of the provider
 * @property lastFetch The last fetch of the provider
 * @property type The type of the provider
 */
data class ProviderOutputModel(
	val id: Int,
	val measures: Any, // GasMeasureListOutputModel or TemperatureMeasureListOutputModel
	val lastFetch: String,
	val type: String
) {
	constructor(provider: Provider) : this(
		id = provider.id,
		measures = when (provider.getProviderType()) {
			ProviderType.GAS -> GasMeasureListOutputModel(provider.measures.toGasMeasures())
			ProviderType.TEMPERATURE -> TemperatureMeasureListOutputModel(provider.measures.toTemperatureMeasures())
		},
		type = provider.getProviderType().toString(),
		lastFetch = provider.lastFetch.toString()
	)
}