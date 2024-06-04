package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.company.DNOCreationDTO
import aguDataSystem.server.domain.contact.ContactCreation
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.tank.Tank

/**
 * Represents the creation information of an AGU.
 *
 * @property cui The CUI of the AGU.
 * @property name The name of the AGU.
 * @property levels The gas levels of the AGU.
 * @property loadVolume The load volume of the AGU.
 * @property location The location of the AGU.
 * @property dno The DNO of the AGU.
 * @property gasLevelUrl The URL of the gas levels of the AGU.
 * @property image The image of the AGU.
 * @property contacts The contacts of the AGU.
 * @property tanks The tanks of the AGU.
 * @property isFavorite Whether the AGU is a favorite.
 * @property notes The notes of the AGU.
 * @property training The training of the AGU.
 */
data class AGUCreationInfo(
	val cui: String,
	val name: String,
	val levels: GasLevels,
	val loadVolume: Int,
	val location: Location,
	val dno: DNOCreationDTO,
	val gasLevelUrl: String,
	val image: ByteArray,
	val contacts: List<ContactCreation>,
	val tanks: List<Tank>,
	val isFavorite: Boolean = false,
	val notes: String?,
	val training: String?,
)