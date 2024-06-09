package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.contact.ContactCreation
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.tank.Tank

/**
 * Represents the creation information of an AGU.
 *
 * @property cui The CUI of the AGU.
 * @property eic The EIC of the AGU.
 * @property name The name of the AGU.
 * @property levels The gas levels of the AGU.
 * @property loadVolume The load volume of the AGU.
 * @property correctionFactor The correction factor of the AGU.
 * @property location The location of the AGU.
 * @property dnoName The DNO name associated with the AGU.
 * @property gasLevelUrl The URL of the gas levels of the AGU.
 * @property image The image of the AGU.
 * @property contacts The contacts of the AGU.
 * @property tanks The tanks of the AGU.
 * @property transportCompanies The transport companies that the AGU is associated with.
 * @property isFavorite Whether the AGU is a favorite.
 * @property isActive Whether the AGU is active.
 * @property notes The notes of the AGU.
 * @property training The training of the AGU.
 */
data class AGUCreationInfo(
	val cui: String,
	val eic: String,
	val name: String,
	val levels: GasLevels,
	val loadVolume: Int,
	val correctionFactor: Double,
	val location: Location,
	val dnoName: String,
	val gasLevelUrl: String,
	val image: ByteArray,
	val contacts: List<ContactCreation>,
	val tanks: List<Tank>,
	val transportCompanies: List<String>,
	val isFavorite: Boolean = false,
	val isActive: Boolean = true,
	val notes: String?,
	val training: String?,
)