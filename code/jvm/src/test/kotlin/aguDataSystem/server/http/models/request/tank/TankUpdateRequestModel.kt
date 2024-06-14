package aguDataSystem.server.http.models.request.tank

import kotlinx.serialization.Serializable

/**
 * Represents the request model for updating a tank.
 *
 * @property minLevel The minimum level of the tank
 * @property maxLevel The maximum level of the tank
 * @property criticalLevel The critical level of the tank
 * @property loadVolume The load volume of the tank
 * @property capacity The capacity of the tank
 * @property correctionFactor The correction factor of the tank
 */
@Serializable
data class TankUpdateRequestModel(
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val loadVolume: Double,
	val capacity: Int,
	val correctionFactor: Double
)