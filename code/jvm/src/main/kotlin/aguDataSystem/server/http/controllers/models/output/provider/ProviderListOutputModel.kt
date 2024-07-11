package aguDataSystem.server.http.controllers.models.output.provider

import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType

/**
 * Output model for a list of providers
 *
 * @param gasProviders The list of gas providers
 * @param temperatureProviders The list of temperature providers
 * @param size The size of the list
 */
data class ProviderListOutputModel(
	val gasProviders: List<GasProviderOutputModel>,
	val temperatureProviders: List<TemperatureProviderOutputModel>,
	val size: Int
) {
	constructor(providers: List<Provider>) : this(
		gasProviders = providers.filter { provider -> provider.getProviderType() == ProviderType.GAS }
			.map { provider -> GasProviderOutputModel(provider) },
		temperatureProviders = providers.filter { provider -> provider.getProviderType() == ProviderType.TEMPERATURE }
			.map { provider -> TemperatureProviderOutputModel(provider) },
		size = providers.size
	)
}