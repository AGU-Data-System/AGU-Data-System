package aguDataSystem.server.domain.tank

import aguDataSystem.server.domain.gasLevels.GasLevels

/**
 * Represents the tank update info
 * @property levels the gas levels
 * @property capacity the capacity
 * @property correctionFactor the correction factor
 */
data class TankUpdateInfo(
	val levels: GasLevels,
	val capacity: Int,
	val correctionFactor: Double
)
