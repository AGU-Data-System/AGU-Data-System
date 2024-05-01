package aguDataSystem.server.http.controllers.agu.models.addAgu

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Tank

/**
 * The input model for creating a tank
 *
 * @param number the number of the tank
 * @param minLevel the minimum level of the tank
 * @param maxLevel the maximum level of the tank
 * @param criticalLevel the critical level of the tank
 * @param loadVolume the load volume of the tank
 * @param capacity the capacity of the tank
 */
data class TankInputModel(
	val number: Int,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val loadVolume: Double,
	val capacity: Int
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
		loadVolume = this.loadVolume.toInt(),
		capacity = this.capacity
	)
}
