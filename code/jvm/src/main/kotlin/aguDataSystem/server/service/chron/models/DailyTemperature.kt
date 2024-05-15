package aguDataSystem.server.service.chron.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the daily temperature data from the provider response.
 *
 * @property time The time of the temperature data.
 * @property max The maximum temperature data.
 * @property min The minimum temperature data.
 */
@Serializable
data class DailyTemperature(
	val time: List<String>,
	@SerialName("temperature_2m_max") val max: List<Double>,
	@SerialName("temperature_2m_min") val min: List<Double>
)