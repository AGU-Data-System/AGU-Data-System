package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.reading.GasReading

/**
 * Represents a Gas Provider
 *
 * @property id the id of the Gas Provider
 * @property readings the readings of the Gas Provider
 */
data class GasProvider(
	override val id: Int,
	override val readings: List<GasReading>
) : Provider()
