package aguDataSystem.server.http.controllers.models.output.tank

import aguDataSystem.server.domain.tank.Tank

/**
 * Represents the output model for a list of [Tank]
 *
 * @property tanks The list of [Tank]
 * @property size The size of the list
 */
data class TankListOutputModel(
	val tanks: List<TankOutputModel>,
	val size: Int
) {
	constructor(tanks: List<Tank>) : this(
		tanks = tanks.map { TankOutputModel(it) },
		size = tanks.size
	)
}