package aguDataSystem.server.domain

/**
 * Data Transfer Object for creating an AGU
 *
 * @property cui CUI of the AGU
 * @property name name of the AGU
 * @property levels gas levels of the AGU
 * @property loadVolume load volume of the AGU
 * @property location location of the AGU
 * @property dnoName name of the DNO
 * @property gasLevelUrl URL of the gas level
 * @property image image of the AGU
 * @property contacts contacts of the AGU
 * @property tanks tanks of the AGU
 * @property isFavorite whether the AGU is a favorite
 * @property notes notes of the AGU
 * @property training training of the AGU
 */
data class AGUCreationDTO(
	val cui: String,
	val name: String,
	val levels: GasLevels,
	val loadVolume: Int,
	val location: Location,
	val dnoName: String,
	val gasLevelUrl: String,
	val image: ByteArray, //TODO: change later to an Image object
	val contacts: List<ContactDTO>,
	val tanks: List<TankDTO>,
	val isFavorite: Boolean = false,
	val notes: String?,
	val training: String?,
)