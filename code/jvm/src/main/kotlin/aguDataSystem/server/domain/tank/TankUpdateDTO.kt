package aguDataSystem.server.domain.tank

import aguDataSystem.server.domain.gasLevels.GasLevels

/**
 * Represents the tank update DTO
 * @property minLevel the minimum level
 * @property maxLevel the maximum level
 * @property criticalLevel the critical level
 * @property capacity the capacity
 * @property correctionFactor the correction factor
 */
data class TankUpdateDTO(
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val capacity: Int,
	val correctionFactor: Double
) {

	/**
	 * Converts the DTO to a [TankUpdateInfo]
	 *
	 * @receiver The DTO to convert
	 * @return The [TankUpdateInfo] representation of the DTO
	 */
	fun toTankUpdateInfo() = TankUpdateInfo(
		levels = GasLevels(
			min = this.minLevel,
			max = this.maxLevel,
			critical = this.criticalLevel
		),
		capacity = this.capacity,
		correctionFactor = this.correctionFactor
	)
}
