package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.contact.Contact

/**
 * Represents the basic information of an AGU.
 *
 * @property cui The CUI of the AGU.
 * @property name The name of the AGU.
 * @property levels The gas levels of the AGU.
 * @property loadVolume The load volume of the AGU.
 * @property location The location of the AGU.
 * @property dnoName The name of the DNO of the AGU.
 * @property gasLevelUrl The URL of the gas levels of the AGU.
 * @property image The image of the AGU.
 * @property contacts The contacts of the AGU.
 * @property tanks The tanks of the AGU.
 * @property isFavorite Whether the AGU is a favorite.
 * @property notes The notes of the AGU.
 * @property training The training of the AGU.
 */
class AGUBasicInfo(
	val cui: String,
	val name: String,
	val levels: GasLevels,
	val loadVolume: Int,
	val location: Location,
	val dnoName: String,
	val gasLevelUrl: String,
	val image: ByteArray, //TODO: change later to an Image object
	val contacts: List<Contact>,
	val tanks: List<Tank>,
	val isFavorite: Boolean = false,
	val notes: String?,
	val training: String?,
)