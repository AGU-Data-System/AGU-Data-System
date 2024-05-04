package aguDataSystem.server.domain.measure

import java.time.LocalDateTime

/**
 * Represents a measure of a gas level.
 *
 * @property level The level of gas detected.
 */
data class GasMeasure(
	override val timestamp: LocalDateTime,
	override val predictionFor: LocalDateTime,
	val level: Int
) : Measure()

/**
 * Converts a list of measures to a list of gas measures.
 *
 * @receiver The list of measures to convert.
 * @return The list of gas measures.
 */
fun List<Measure>.toGasReadings(): List<GasMeasure> {
	if (this.all { it is GasMeasure }) {
		return this.map { it as GasMeasure }
	} else {
		throw IllegalArgumentException("Not all measures are gas measures")
	}
}
