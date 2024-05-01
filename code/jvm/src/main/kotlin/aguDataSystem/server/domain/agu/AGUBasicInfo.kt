package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.contact.Contact

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