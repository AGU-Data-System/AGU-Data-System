package aguDataSystem.server.http.models.response.agu

import kotlinx.serialization.Serializable

/**
 * Response model for AGUBasicInfoList
 *
 * @param agusBasicInfo List of AGUBasicInfoResponse
 * @param size Size of the list
 */
@Serializable
data class AGUBasicInfoListResponse(
	val agusBasicInfo: List<AGUBasicInfoResponse>,
	val size: Int
)