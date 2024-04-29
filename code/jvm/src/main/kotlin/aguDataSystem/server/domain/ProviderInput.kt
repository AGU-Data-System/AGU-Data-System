package aguDataSystem.server.domain

import kotlinx.serialization.Serializable

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
