package aguDataSystem.server.http.models.request.dno

import kotlinx.serialization.Serializable

/**
 * Request model for creating DNO
 *
 * @param name: String
 * @param region: String
 */
@Serializable
data class DNOCreationRequestModel(
	val name: String,
	val region: String
)