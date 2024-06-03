package aguDataSystem.server.http.controllers.agu.models.output.provider

import aguDataSystem.server.domain.measure.GasMeasure

/**
 * Output model for GasMeasure
 *
 * @param timestamp The timestamp of the measure
 * @param predictionFor The prediction for the measure
 * @param level The level of the measure
 * @param tankNumber The tank number of the measure
 */
data class GasMeasureOutputModel(
	val timestamp: String,
	val predictionFor: String?,
	val level: Int,
	val tankNumber: Int
) {
	constructor(measure: GasMeasure) : this(
		timestamp = measure.timestamp.toString(),
		predictionFor = measure.predictionFor?.toString(),
		level = measure.level,
		tankNumber = measure.tankNumber
	)
}