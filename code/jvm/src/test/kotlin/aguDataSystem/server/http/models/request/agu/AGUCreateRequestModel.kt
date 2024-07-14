package aguDataSystem.server.http.models.request.agu

import aguDataSystem.server.http.models.request.contact.ContactCreationRequestModel
import aguDataSystem.server.http.models.request.tank.TankCreationRequestModel
import kotlinx.serialization.Serializable

/**
 * Request model for creating an AGU
 *
 * @param cui The CUI of the AGU
 * @param name The name of the AGU
 * @param minLevel The minimum gas level of the AGU
 * @param maxLevel The maximum gas level of the AGU
 * @param criticalLevel The critical gas level of the AGU
 * @param latitude The latitude of the AGU
 * @param longitude The longitude of the AGU
 * @param locationName The name of the location of the AGU
 * @param dnoName The DNO name that the AGU is associated with
 * @param gasLevelUrl The URL of the gas level of the AGU
 * @param image The image of the AGU
 * @param tanks The tanks of the AGU
 * @param contacts The contacts of the AGU
 * @param isFavourite The favorite status of the AGU
 * @param notes The notes of the AGU
 */
@Serializable
data class AGUCreateRequestModel(
	val cui: String,
	val eic: String,
	val name: String,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val loadVolume: Int,
	val correctionFactor: Double,
	val latitude: Double,
	val longitude: Double,
	val locationName: String,
	val dnoName: String,
	val gasLevelUrl: String,
	val image: ByteArray,
	val tanks: List<TankCreationRequestModel>,
	val contacts: List<ContactCreationRequestModel>,
	val transportCompanies: List<String>,
	val isFavourite: Boolean,
	val isActive: Boolean,
	val notes: String?,
)