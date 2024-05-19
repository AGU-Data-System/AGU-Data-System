package aguDataSystem.server.domain.tank

import aguDataSystem.server.domain.gasLevels.GasLevels

/**
 * Represents a tank
 *
 * @property number The number of the tank
 * @property levels The gas levels of the tank
 * @property loadVolume The load volume of the tank
 * @property capacity The capacity of the tank
 */
data class Tank(
	val number: Int,
	val levels: GasLevels,
	val loadVolume: Int,
	val capacity: Int,
	val correctionFactor: Double
)