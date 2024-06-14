package aguDataSystem.server.http.models.request.transportCompany

import kotlinx.serialization.Serializable

/**
 * Request model for creating a transport company
 *
 * @param name the name of the transport company
 */
@Serializable
data class TransportCompanyRequestModel(
	val name: String
)