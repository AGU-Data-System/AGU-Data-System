package aguDataSystem.server.http.controllers.models.output.provider

import aguDataSystem.server.domain.measure.toGasMeasures
import aguDataSystem.server.domain.provider.Provider

/**
 * Output model for a Gas Provider
 *
 * @property id The id of the provider
 * @property measures The measures of the provider
 * @property lastFetch The last fetch of the provider
 */
data class GasProviderOutputModel(
	val id: Int,
	val measures: GasMeasureListOutputModel,
	val lastFetch: String
) {
	constructor(provider: Provider) : this(
		id = provider.id,
		measures = GasMeasureListOutputModel(provider.measures.toGasMeasures()),
		lastFetch = provider.lastFetch.toString()
	)
}