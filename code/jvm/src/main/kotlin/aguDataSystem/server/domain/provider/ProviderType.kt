package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.reading.GasReading
import aguDataSystem.server.domain.reading.Reading
import aguDataSystem.server.domain.reading.TemperatureReading
import aguDataSystem.server.domain.reading.toGasReadings
import aguDataSystem.server.domain.reading.toTemperatureReadings
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
	 * @param readings The readings of the Provider
	 * @return The created Provider
	 */
	fun createProviderWithReadings(id: Int, readings: List<Reading>): Provider {
		return when (this) {
			GAS -> GasProvider(id = id, readings = readings.toGasReadings())
			TEMPERATURE -> TemperatureProvider(id = id, readings = readings.toTemperatureReadings())
		}
	}

	/**
	 * Builds a reading based on a map.
	 *
	 * @param timestamp The time when the reading was taken.
	 * @param predictionFor The time for which the reading is
	 * a prediction if its null it's not a prediction.
	 * @param values The list containing the reading data.
	 * @return The reading.
	 */
	fun buildReading(timestamp: LocalDateTime, predictionFor: LocalDateTime, values: List<Int>): Reading {
		return when (this) {
			GAS -> GasReading(
				timestamp = timestamp,
				predictionFor = predictionFor,
				level = values[0]
			)

			TEMPERATURE -> TemperatureReading(
				timestamp = timestamp,
				predictionFor = predictionFor,
				min = values[0],
				max = values[1]
			)
		}
	}
}


