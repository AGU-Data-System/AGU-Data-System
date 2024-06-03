package aguDataSystem.server.http.controllers.agu.models.output.provider

import aguDataSystem.server.domain.measure.TemperatureMeasure

/**
 * Output model for TemperatureMeasure
 *
 * @param timestamp The timestamp of the measure
 * @param predictionFor The prediction for the measure
 * @param min The minimum temperature
 * @param max The maximum temperature
 */
class TemperatureMeasureOutputModel(
	val timestamp: String,
	val predictionFor: String?,
	val min: Int,
	val max: Int,
) {
	constructor(measure: TemperatureMeasure) : this(
		timestamp = measure.timestamp.toString(),
		predictionFor = measure.predictionFor?.toString(),
		min = measure.min,
		max = measure.max
	)
}
