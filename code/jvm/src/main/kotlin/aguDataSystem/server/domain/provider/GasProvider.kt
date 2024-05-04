package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.measure.GasMeasure

/**
 * Represents a Gas Provider
 *
 * @property id the id of the Gas Provider
 * @property measures the readings of the Gas Provider
 */
data class GasProvider(
	override val id: Int,
	override val measures: List<GasMeasure>
) : Provider()
