package aguDataSystem.server.domain

// import java.awt.Image

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
 * @property latitude latitude of the AGU
 * @property longitude longitude of the AGU
 * @property locationName name of the location of the AGU
 * @property dnoId DNO id of the AGU
 * @property notes notes of the AGU
 * @property training training of the AGU
 * @property image image of the AGU
 * @property contacts contacts of the AGU
 */
data class AGU(
	val cui: String,
	val name: String,
	val isFavorite: Boolean = false,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val capacity: Double,
	val latitude: Double,
	val longitude: Double,
	val locationName: String,
	val dnoId: Int,
	val notes: String,
	val training: String,
	val image: ByteArray, //change later to an Image object
	val contacts: List<Contact>,
	// val tanks : List<Tank> // add tanks to the AGU
	// transform location parameters to a Location object
	// create an AGU domain object
	// TODO needs revision
)