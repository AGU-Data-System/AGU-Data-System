package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.measure.Measure
import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.domain.measure.toGasReadings
import aguDataSystem.server.domain.measure.toTemperatureReadings
import java.time.LocalDateTime

/**
 * Represents a ProviderType
 */
enum class ProviderType {
	GAS,
	TEMPERATURE;

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
			GAS -> GasProvider(id = id, measures = measures.toGasReadings(), lastFetch = lastFetch)
			TEMPERATURE -> TemperatureProvider(
				id = id,
				measures = measures.toTemperatureReadings(),
				lastFetch = lastFetch
			)
		}
	}

	/**
	 * Builds a measures based on a map.
	 *
	 * @param timestamp The time when the reading was taken.
	 * @param predictionFor The time for which the measure is
	 * a prediction if its null it's not a prediction.
	 * @param values The list containing the measure data.
	 * @return The reading.
	 */
	fun buildMeasure(timestamp: LocalDateTime, predictionFor: LocalDateTime, vararg values: Int): Measure {
		require(values.size in 1..2) { "Invalid number of values" }
		return when (this) {
			GAS -> GasMeasure(
				timestamp = timestamp,
				predictionFor = predictionFor,
				level = values.first()
			)

			TEMPERATURE -> TemperatureMeasure(
				timestamp = timestamp,
				predictionFor = predictionFor,
				min = values.first(),
				max = values.last()
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
