package aguDataSystem.server.service.chron.models.prediction

import aguDataSystem.server.domain.measure.TemperatureMeasure
import kotlinx.serialization.Serializable

/**
 * Data class for the training temperatures.
 *
 * @property temperatures The temperatures for training
 * @property consumptions The consumptions for training
 */
@Serializable
data class TrainingRequestModel(
	val temperatures: String,
	val consumptions: String
) {
	constructor(temperatures: List<TemperatureMeasure>, consumptions: List<Int>) : this(
		temperatures = temperatures.joinToString(",") { "${it.min},${it.max}" },
		consumptions = consumptions.joinToString(",")
	)
}