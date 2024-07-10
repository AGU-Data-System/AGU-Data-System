package aguDataSystem.server.service.chron.models.fetcher

import kotlinx.serialization.Serializable

/**
 * Model for the temperature data from a provider response.
 *
 * @property daily The daily temperature data
 */
@Serializable
data class TemperatureData(
	val daily: DailyTemperature,
)
