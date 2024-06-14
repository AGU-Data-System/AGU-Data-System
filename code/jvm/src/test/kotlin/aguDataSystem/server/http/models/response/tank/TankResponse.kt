package aguDataSystem.server.http.models.response.tank

import aguDataSystem.server.http.models.response.gasLevels.GasLevelsResponse
import kotlinx.serialization.Serializable

/**
 * Represents a response containing a tank
 *
 * @property number the tank number
 * @property levels the gas levels
 * @property capacity the tank capacity
 * @property correctionFactor the correction factor
 */
@Serializable
class TankResponse(
	val number: Int,
	val levels: GasLevelsResponse,
	val capacity: Int,
	val correctionFactor: Double
)