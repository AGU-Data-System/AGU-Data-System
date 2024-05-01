package aguDataSystem.server.domain.reading

import java.time.LocalDateTime

/**
 * Represents a reading of a gas sensor.
 *
 * @property level The level of gas detected.
 */
data class GasReading(
	override val timestamp: LocalDateTime,
	override val predictionFor: LocalDateTime,
	val level: Int
) : Reading()

/**
 * Converts a list of readings to a list of gas readings.
 *
 * @receiver The list of readings to convert.
 * @return The list of gas readings.
 */
fun List<Reading>.toGasReadings(): List<GasReading> {
	if (this.all { it is GasReading }) {
		return this.map { it as GasReading }
	} else {
		throw IllegalArgumentException("Not all readings are gas readings")
	}
}
