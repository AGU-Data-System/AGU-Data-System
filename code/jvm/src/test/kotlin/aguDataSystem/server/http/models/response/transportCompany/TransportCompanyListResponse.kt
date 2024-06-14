package aguDataSystem.server.http.models.response.transportCompany

import kotlinx.serialization.Serializable

/**
 * Response for a list of transport companies
 *
 * @property transportCompanies the list of transport companies
 * @property size the size of the list
 */
@Serializable
data class TransportCompanyListResponse(
	val transportCompanies: List<TransportCompanyResponse>,
	val size: Int
)