package aguDataSystem.server.http.models.response.transportCompany

import kotlinx.serialization.Serializable

/**
 * Response model for a transport company
 *
 * @property id the id of the transport company
 * @property name the name of the transport company
 */
@Serializable
data class TransportCompanyResponse(
	val id: Int,
	val name: String
)