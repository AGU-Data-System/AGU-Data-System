package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.reading.TemperatureReading

/**
 * Represents a temperature Provider
 *
 * @property id the id of the temperature Provider
 * @property readings the readings of the temperature Provider
 */
data class TemperatureProvider(
	override val id: Int,
	override val readings: List<TemperatureReading>
) : Provider()