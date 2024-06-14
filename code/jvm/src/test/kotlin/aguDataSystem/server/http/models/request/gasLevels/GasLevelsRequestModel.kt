package aguDataSystem.server.http.models.request.gasLevels

import kotlinx.serialization.Serializable

/**
 * Request model for gas levels
 *
 * @param min Min gas level of the AGU or Tank
 * @param max Max gas level of the AGU or Tank
 * @param critical Critical gas level of the AGU or Tank
 */
@Serializable
data class GasLevelsRequestModel(
	val min: Int,
	val max: Int,
	val critical: Int
)