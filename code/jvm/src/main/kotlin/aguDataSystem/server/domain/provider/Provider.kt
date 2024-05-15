package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.measure.Measure
import java.time.LocalDateTime

/**
 * Represents a Provider
 *
 * @property id the id of the Provider
 * @property measures the readings of the Provider
 */
sealed class Provider {
	abstract val id: Int
	open val measures: List<Measure> = emptyList()
	abstract val lastFetch: LocalDateTime?

	/**
	 * Gets the type of Provider based on the class
	 *
	 * @receiver the Provider
	 * @return the type of Provider
	 */
	fun getProviderType(): ProviderType {
		return when (this) {
			is TemperatureProvider -> ProviderType.TEMPERATURE
			is GasProvider -> ProviderType.GAS
		}
	}
}
