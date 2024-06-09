package aguDataSystem.server.http.models.tank

import kotlinx.serialization.Serializable

@Serializable
data class TankCreationRequestModel(
	val number: Int,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val loadVolume: Double,
	val capacity: Int,
	val correctionFactor: Double,
)