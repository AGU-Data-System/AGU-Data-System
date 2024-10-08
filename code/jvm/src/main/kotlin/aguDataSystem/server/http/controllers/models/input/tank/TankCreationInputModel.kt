package aguDataSystem.server.http.controllers.models.input.tank

import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.tank.Tank

/**
 * The input model for creating a tank
 *
 * @param number the number of the tank
 * @param minLevel the minimum level of the tank
 * @param maxLevel the maximum level of the tank
 * @param criticalLevel the critical level of the tank
 * @param capacity the capacity of the tank
 */
data class TankCreationInputModel(
	val number: Int,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val capacity: Int,
	val correctionFactor: Double
) {

	/**
	 * Converts the input model to a data transfer object
	 *
	 * @receiver the tank input model
	 * @return the tank data transfer object
	 */
	fun toTank() = Tank(
		number = this.number,
		levels = GasLevels(
			min = this.minLevel,
			max = this.maxLevel,
			critical = this.criticalLevel
		),
		capacity = this.capacity,
		correctionFactor = this.correctionFactor
	)
}
