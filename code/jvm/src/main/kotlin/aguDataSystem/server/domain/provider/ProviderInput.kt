package aguDataSystem.server.domain.provider

import kotlinx.serialization.Serializable

/**
 * TODO
 */
@Serializable
abstract class ProviderInput(
	open val name: String,
	open val url: String,
	open val frequency: String,
	open val isActive: Boolean
) {
	companion object {
		const val DEFAULT_ACTIVE = true
	}
}

/**
 * TODO
 */
@Serializable
data class GasProviderInput(
	val baseName: String,
	val baseUrl: String
) : ProviderInput(
	name = "gas - $baseName",
	url = baseUrl,
	frequency = GAS_FREQUENCY,
	isActive = DEFAULT_ACTIVE
) {
	companion object {
		const val GAS_FREQUENCY = "PT1H"
	}
}

/**
 * TODO
 */
@Serializable
data class TemperatureProviderInput(
	val baseName: String,
	val baseUrl: String
) : ProviderInput(
	name = "temperature - $baseName",
	url = baseUrl,
	frequency = TEMPERATURE_FREQUENCY,
	isActive = DEFAULT_ACTIVE
) {
	companion object {
		const val TEMPERATURE_FREQUENCY = "P1D"
	}
}
