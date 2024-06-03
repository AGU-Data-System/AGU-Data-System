package aguDataSystem.server.http.controllers.agu.models.output.tank

import aguDataSystem.server.domain.tank.Tank
import aguDataSystem.server.http.controllers.agu.models.output.gasLevels.GasLevelsOutputModel

/**
 * Represents the output model for a [Tank]
 *
 * @property number The tank number
 * @property levels The gas levels
 * @property loadVolume The load volume
 * @property capacity The tank capacity
 * @property correctionFactor The correction factor
 */
data class TankOutputModel(
	val number: Int,
	val levels: GasLevelsOutputModel,
	val loadVolume: Int,
	val capacity: Int,
	val correctionFactor: Double
) {
	constructor(tank: Tank) : this(
		number = tank.number,
		levels = GasLevelsOutputModel(tank.levels),
		loadVolume = tank.loadVolume,
		capacity = tank.capacity,
		correctionFactor = tank.correctionFactor
	)
}