package aguDataSystem.server.http.models.gasLevels

import kotlinx.serialization.Serializable

@Serializable
data class GasLevelsRequestModel(
	val min: Int,
	val max: Int,
	val critical: Int
)