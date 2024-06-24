package aguDataSystem.server.http.models.response.transportCompany

import kotlinx.serialization.Serializable

/**
 * Response model for the creation of a transport company
 */
@Serializable
data class TransportCompanyCreationResponse(
	val id: Int
)
