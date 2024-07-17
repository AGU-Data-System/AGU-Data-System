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
        temperatures = temperatures.toTemperatureRequest(),
        consumptions = consumptions.toConsumptionRequest()
    )

    override fun toString(): String {
        return "{\"temperatures\":$temperatures, \"consumptions\":$consumptions}"
    }
}

/**
 * Converts a list of temperature request models to a temperature request json string.
 *
 * @receiver The list of temperature request models
 * @return The temperature request json string
 */
fun List<TemperatureRequestModel>.toTemperatureRequest(): String {
    val minTemps = this.map { it.min }
    val maxTemps = this.map { it.max }
    val timeStamps = this.map { it.timeStamp }.map { "\"$it\"" }
    return "{\"date\": $timeStamps, \"minTemps\": $minTemps, \"maxTemps\": $maxTemps}"
}

/**
 * Converts a list of consumption request models to a consumption request json string.
 *
 * @receiver The list of consumption request models
 * @return The consumption request json string
 */
fun List<ConsumptionRequestModel>.toConsumptionRequest(): String {
    val consumptions = this.map { it.level }
    val timeStamps = this.map { it.timestamp }.map { "\"$it\"" }
    return "{\"date\": $timeStamps, \"consumption\": $consumptions}"
}