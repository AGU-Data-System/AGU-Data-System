package aguDataSystem.server.service.chron.models.prediction

import kotlinx.serialization.Serializable

/**
 * Data class for the prediction request.
 *
 * @property temperatures The temperatures
 * @property previousConsumptions The previous consumptions
 * @property coefficients The coefficients
 * @property intercept The intercept
 */
@Serializable
data class PredictionRequestModel(
	val temperatures: String,
	val previousConsumptions: String,
	val coefficients: List<Double>,
	val intercept: Double
) {
	constructor(
		temperatures: List<TemperatureRequestModel>,
		previousConsumptions: List<ConsumptionRequestModel>,
		coefficients: List<Double>,
		intercept: Double
	) : this(
		temperatures = temperatures.toTemperatureRequest(),
		previousConsumptions = previousConsumptions.toConsumptionRequest(),
		coefficients = coefficients,
		intercept = intercept
	)
}