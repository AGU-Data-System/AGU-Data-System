package aguDataSystem.server.http.controllers.models.output.provider

import aguDataSystem.server.domain.measure.TemperatureMeasure

/**
 * Output model for TemperatureMeasureList
 *
 * @param temperatureMeasures The list of TemperatureMeasureOutputModel
 * @param size The size of the list
 */
data class TemperatureMeasureListOutputModel(
	val temperatureMeasures: List<TemperatureMeasureOutputModel>,
	val size: Int
) {
	constructor(temperatureMeasures: List<TemperatureMeasure>) : this(
		temperatureMeasures = temperatureMeasures.map { measure -> TemperatureMeasureOutputModel(measure) },
		size = temperatureMeasures.size
	)
}