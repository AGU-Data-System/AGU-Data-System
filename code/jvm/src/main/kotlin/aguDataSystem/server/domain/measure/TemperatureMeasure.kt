package aguDataSystem.server.domain.measure

import java.time.LocalDateTime

/**
 * Represents a temperature measure.
 *
 * @property timestamp The time when the reading was taken.
 * @property predictionFor The time for which the reading is a prediction, if it's the same as timestamp it's not a prediction.
 * @property min The minimum temperature for the day.
 * @property max The maximum temperature for the day.
 */
data class TemperatureMeasure(
	override val timestamp: LocalDateTime,
	override val predictionFor: LocalDateTime,
	val min: Int,
	val max: Int
) : Measure()

/**
 * Converts a list of measures to a list of temperature measure.
 *
 * @receiver The list of measure to convert.
 * @return The list of temperature measure.
 */
fun List<Measure>.toTemperatureMeasures(): List<TemperatureMeasure> {
	if (this.all { it is TemperatureMeasure }) {
		return this.map { it as TemperatureMeasure }
	} else {
		throw IllegalArgumentException("Not all measure are temperature measure")
	}
}
