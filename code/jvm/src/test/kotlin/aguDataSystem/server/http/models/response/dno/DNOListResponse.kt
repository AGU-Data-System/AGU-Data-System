package aguDataSystem.server.http.models.response.dno

import kotlinx.serialization.Serializable

/**
 * Response model for DNOList
 *
 * @param dnos List of DNOResponse
 * @param size Size of the list
 */
@Serializable
data class DNOListResponse(
	val dnos: List<DNOResponse>,
	val size: Int
)