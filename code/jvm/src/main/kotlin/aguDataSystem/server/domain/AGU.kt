package aguDataSystem.server.domain

/**
 * Represents the Autonomous Gas Unit (AGU) in the system.
 *
 * @property name name of the AGU
 * @property cui CUI of the AGU
 * @property isFavorite whether the AGU is a favorite
 * @property minLevel minimum level of the AGU
 * @property maxLevel maximum level of the AGU
 * @property criticalLevel critical level of the AGU
 * @property capacity capacity of the AGU
 * @property location location of the AGU
 * @property dnoId DNO id of the AGU
 * @property notes notes of the AGU
 * @property training training of the AGU
 * @property image image of the AGU
 * @property contacts contacts of the AGU
 */
data class AGU(
	val cui: String,
	val name: String,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val loadVolume: Int,
	val location: Location,
	val dno: DNO,
	val isFavorite: Boolean = false,
	val notes: String,
	val training: String,
	val image: ByteArray, //TODO: change later to an Image object
	val contacts: List<Contact>,
	val tanks : List<Tank>,
	val providers: List<Provider>
) {

	// calculate the total capacity of the AGU
	val capacity = tanks.sumOf { it.capacity }
}