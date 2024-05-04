package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.measure.TemperatureMeasure

/**
 * Represents a temperature Provider
 *
 * @property id the id of the temperature Provider
 * @property measures the readings of the temperature Provider
 */
data class TemperatureProvider(
	override val id: Int,
	override val measures: List<TemperatureMeasure>
) : Provider()