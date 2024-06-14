package aguDataSystem.server.http.models.response.dno

import kotlinx.serialization.Serializable

/**
 * DNO response model
 *
 * @property id the id of the DNO
 * @property name the name of the DNO
 * @property region the region of the DNO
 */
@Serializable
data class DNOResponse(
	val id: Int,
	val name: String,
	val region: String
)