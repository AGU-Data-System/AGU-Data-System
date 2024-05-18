package aguDataSystem.server.domain.provider

import kotlinx.serialization.Serializable

/**
 * Represents a Provider Input used to create a Provider
 * in the Scheduling Fetcher
 *
 * @property name the name of the Provider
 * @property url the url of the Provider
 * @property frequency the frequency of the Provider
 * @property isActive if the Provider is active
 */
@Serializable
data class ProviderInput(
	val name: String,
	private val url: String,
	private val frequency: String,
	private val isActive: Boolean
) {
	companion object {
		const val DEFAULT_ACTIVE = true
		const val GAS_FREQUENCY = "PT10M"
		const val TEMPERATURE_FREQUENCY = "P1D"
	}

	constructor(name: String, url: String, type: ProviderType) : this(
		name = name,
		url = url,
		frequency = when (type) {
			ProviderType.GAS -> GAS_FREQUENCY
			ProviderType.TEMPERATURE -> TEMPERATURE_FREQUENCY
		},
		isActive = DEFAULT_ACTIVE
	)
}
