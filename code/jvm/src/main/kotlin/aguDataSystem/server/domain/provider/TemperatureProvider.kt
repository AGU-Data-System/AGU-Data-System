package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.measure.TemperatureMeasure
import java.time.LocalDateTime

/**
 * Represents a temperature Provider
 *
 * @property id the id of the temperature Provider
 * @property measures the readings of the temperature Provider
 * @property lastFetch the last time the temperature Provider was fetched
 */
data class TemperatureProvider(
	override val id: Int,
	override val measures: List<TemperatureMeasure>,
	override val lastFetch: LocalDateTime?
) : Provider()