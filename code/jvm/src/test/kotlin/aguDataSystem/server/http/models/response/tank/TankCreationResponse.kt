package aguDataSystem.server.http.models.response.tank

import kotlinx.serialization.Serializable

/**
 * Response for the creation of a tank
 *
 * @property number the number of the tank
 */
@Serializable
data class TankCreationResponse(
	val number: Int
)