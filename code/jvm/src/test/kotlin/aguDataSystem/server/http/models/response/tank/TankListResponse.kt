package aguDataSystem.server.http.models.response.tank

import kotlinx.serialization.Serializable

/**
 * Represents a response containing a list of tanks
 *
 * @property tanks the list of tanks
 * @property size the size of the list
 */
@Serializable
class TankListResponse(
	val tanks: List<TankResponse>,
	val size: Int
)