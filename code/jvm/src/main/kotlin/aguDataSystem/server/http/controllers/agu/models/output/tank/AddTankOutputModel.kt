package aguDataSystem.server.http.controllers.agu.models.output.tank

import aguDataSystem.server.domain.tank.Tank

/**
 * Represents the output model for adding a [Tank]
 *
 * @property number The number of the [Tank]
 */
class AddTankOutputModel(
	val number: Int
)