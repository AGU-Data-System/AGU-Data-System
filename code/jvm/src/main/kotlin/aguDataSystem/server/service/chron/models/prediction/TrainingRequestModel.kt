package aguDataSystem.server.service.chron.models.prediction

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
	constructor(temperatures: List<TemperatureRequestModel>, consumptions: List<ConsumptionRequestModel>) : this(
		temperatures = temperatures.toRequest(),
		consumptions = consumptions.toRequest()
	)

	override fun toString(): String {
		return "{\"temperatures\":$temperatures, \"consumptions\":$consumptions}"
	}
}

fun List<TemperatureRequestModel>.toRequest(): String {
	val minTemps = this.map { it.min }
	val maxTemps = this.map { it.max }
	val timeStamps = this.map { it.timeStamp }.map { "\"$it\"" }
	return "{\"date\": $timeStamps, \"minTemps\": $minTemps, \"maxTemps\": $maxTemps}"
}

fun List<ConsumptionRequestModel>.toRequest(): String {
	val consumptions = this.map { it.level }
	val timeStamps = this.map { it.timestamp }.map { "\"$it\"" }
	return "{\"date\": $timeStamps, \"consumptions\": $consumptions}"
}