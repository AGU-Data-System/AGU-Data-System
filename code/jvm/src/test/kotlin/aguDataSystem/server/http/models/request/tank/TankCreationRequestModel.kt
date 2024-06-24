package aguDataSystem.server.http.models.request.tank

import kotlinx.serialization.Serializable

/**
 * Request model for creating a tank
 *
 * @param number the number of the tank in the AGU
 * @param minLevel the minimum level of the tank
 * @param maxLevel the maximum level of the tank
 * @param criticalLevel the critical level of the tank
 * @param capacity the capacity of the tank
 * @param correctionFactor the correction factor of the tank
 */
@Serializable
data class TankCreationRequestModel(
	val number: Int,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val capacity: Int,
	val correctionFactor: Double,
)