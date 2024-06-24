package aguDataSystem.server.http.models.response.agu


import aguDataSystem.server.http.models.response.dno.DNOResponse
import aguDataSystem.server.http.models.response.location.LocationResponse
import aguDataSystem.server.http.models.response.transportCompany.TransportCompanyResponse
import kotlinx.serialization.Serializable

/**
 * Response model for AGUBasicInfoList
 *
 * @param cui The CUI of the AGU
 * @param eic The EIC of the AGU
 * @param name The name of the AGU
 * @param isFavourite The favorite status of the AGU
 * @param dno The DNO of the AGU
 * @param location The location of the AGU
 * @param transportCompanies The transport companies of the AGU
 */
@Serializable
data class AGUBasicInfoResponse(
	val cui: String,
	val eic: String,
	val name: String,
	val isFavourite: Boolean,
	val dno: DNOResponse,
	val location: LocationResponse,
	val transportCompanies: List<TransportCompanyResponse>
)