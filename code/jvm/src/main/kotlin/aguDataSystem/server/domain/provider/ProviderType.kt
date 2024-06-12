package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.measure.Measure
import aguDataSystem.server.domain.measure.toGasMeasures
import aguDataSystem.server.domain.measure.toTemperatureMeasures
import java.time.Duration
import java.time.LocalDateTime

/**
 * Represents a ProviderType
 */
enum class ProviderType(val pollingFrequency: Duration) {
	GAS(Duration.ofMinutes(10)),
	TEMPERATURE(Duration.ofDays(1));

	/**
	 * Creates a Provider based on a type
	 *
	 * @receiver The type of the Provider
	 * @param id The id of the Provider
	 * @param measures The readings of the Provider
	 * @param lastFetch The time when the readings were last fetched
	 * @return The created Provider
	 */
	fun createProviderWithReadings(id: Int, measures: List<Measure>, lastFetch: LocalDateTime?): Provider {
		return when (this) {
			GAS -> GasProvider(id = id, measures = measures.toGasMeasures(), lastFetch = lastFetch)
			TEMPERATURE -> TemperatureProvider(
				id = id,
				measures = measures.toTemperatureMeasures(),
				lastFetch = lastFetch
			)
		}
	}
}

/**
 * Converts a string to a ProviderType
 *
 * @receiver The string to convert
 * @return The ProviderType
 */
fun String.toProviderType(): ProviderType = ProviderType.valueOf(this.uppercase())
