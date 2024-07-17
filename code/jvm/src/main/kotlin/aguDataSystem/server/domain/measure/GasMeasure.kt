package aguDataSystem.server.domain.measure

import java.time.LocalDateTime

/**
 * Represents a measure of a gas level.
 *
 * @property timestamp The time when the reading was taken.
 * @property predictionFor The time for which the reading is a prediction, if it's the same as timestamp it's not a prediction.
 * @property level The level of gas detected.
 * @property tankNumber The number of the tank where the gas was detected.
 */
data class GasMeasure(
	override val timestamp: LocalDateTime,
	override val predictionFor: LocalDateTime,
	val level: Int,
	val tankNumber: Int
) : Measure()

/**
 * Converts a list of measures to a list of gas measures.
 *
 * @receiver The list of measures to convert.
 * @return The list of gas measures.
 */
fun List<Measure>.toGasMeasures(): List<GasMeasure> {
	if (this.all { it is GasMeasure }) {
		return this.map { it as GasMeasure }
	} else {
		throw IllegalArgumentException("Not all measures are gas measures")
	}
}
