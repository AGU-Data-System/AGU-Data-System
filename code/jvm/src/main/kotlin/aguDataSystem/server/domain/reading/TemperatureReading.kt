package aguDataSystem.server.domain.reading

import java.time.LocalDateTime

/**
 * Represents a reading of a temperature station.
 *
 * @property min The minimum temperature for the day.
 * @property max The maximum temperature for the day.
 */
data class TemperatureReading(
	override val timestamp: LocalDateTime,
	override val predictionFor: LocalDateTime,
	val min: Int,
	val max: Int
) : Reading()

/**
 * Converts a list of readings to a list of temperature readings.
 *
 * @receiver The list of readings to convert.
 * @return The list of temperature readings.
 */
fun List<Reading>.toTemperatureReadings(): List<TemperatureReading> {
	return this.map { it as TemperatureReading }
}
