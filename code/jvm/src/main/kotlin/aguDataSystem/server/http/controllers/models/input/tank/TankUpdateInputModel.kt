package aguDataSystem.server.http.controllers.models.input.tank

import aguDataSystem.server.domain.tank.TankUpdateDTO

/**
 * Represents the input model for updating a tank
 *
 * @property minLevel The minimum level of the tank
 * @property maxLevel The maximum level of the tank
 * @property criticalLevel The critical level of the tank
 * @property capacity The capacity of the tank
 * @property correctionFactor The correction factor of the tank
 */
data class TankUpdateInputModel(
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val capacity: Int,
	val correctionFactor: Double
) {

	/**
	 * Converts the input model to a [TankUpdateDTO]
	 *
	 * @receiver The input model to convert
	 * @return The [TankUpdateDTO] representation of the input model
	 */
	fun toTankUpdateDTO() = TankUpdateDTO(
		minLevel = this.minLevel,
		maxLevel = this.maxLevel,
		criticalLevel = this.criticalLevel,
		capacity = this.capacity,
		correctionFactor = this.correctionFactor
	)
}