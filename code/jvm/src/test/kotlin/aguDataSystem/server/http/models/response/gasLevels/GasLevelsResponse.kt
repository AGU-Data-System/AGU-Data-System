package aguDataSystem.server.http.models.response.gasLevels

import kotlinx.serialization.Serializable

/**
 * Gas levels response
 *
 * @param min Minimum gas level
 * @param max Maximum gas level
 * @param critical Critical gas level
 */
@Serializable
data class GasLevelsResponse(
	val min: Int,
	val max: Int,
	val critical: Int
)