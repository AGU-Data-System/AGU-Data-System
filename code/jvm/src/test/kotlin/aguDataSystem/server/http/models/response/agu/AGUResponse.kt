package aguDataSystem.server.http.models.response.agu

import aguDataSystem.server.http.models.response.ByteArrayAsEmptyArraySerializer
import aguDataSystem.server.http.models.response.contact.ContactListResponse
import aguDataSystem.server.http.models.response.dno.DNOResponse
import aguDataSystem.server.http.models.response.gasLevels.GasLevelsResponse
import aguDataSystem.server.http.models.response.location.LocationResponse
import aguDataSystem.server.http.models.response.provider.ProviderListResponse
import aguDataSystem.server.http.models.response.tank.TankListResponse
import aguDataSystem.server.http.models.response.transportCompany.TransportCompanyListResponse
import kotlinx.serialization.Serializable

/**
 * Response model for AGU
 *
 * @property cui the CUI of the AGU
 * @property eic the EIC of the AGU
 * @property name the name of the AGU
 * @property levels the gas levels of the AGU
 * @property loadVolume the load volume of the AGU
 * @property correctionFactor the correction factor of the AGU
 * @property location the location of the AGU
 * @property dno the DNO of the AGU
 * @property image the image of the AGU
 * @property contacts the contacts of the AGU
 * @property tanks the tanks of the AGU
 * @property providers the providers of the AGU
 * @property transportCompanies the transport companies of the AGU
 * @property isFavourite the favourite status of the AGU
 * @property isActive the active status of the AGU
 * @property notes the notes of the AGU
 * @property training the training of the AGU
 * @property capacity the capacity of the AGU
 */
@Serializable
data class AGUResponse(
	val cui: String,
	val eic: String,
	val name: String,
	val levels: GasLevelsResponse,
	val loadVolume: Int,
	val correctionFactor: Double,
	val location: LocationResponse,
	val dno: DNOResponse,
	@Serializable(with = ByteArrayAsEmptyArraySerializer::class)
	val image: ByteArray,
	val contacts: ContactListResponse,
	val tanks: TankListResponse,
	val providers: ProviderListResponse,
	val transportCompanies: TransportCompanyListResponse,
	val isFavourite: Boolean,
	val isActive: Boolean,
	val notes: String?,
	val training: String?,
	val capacity: Int
)