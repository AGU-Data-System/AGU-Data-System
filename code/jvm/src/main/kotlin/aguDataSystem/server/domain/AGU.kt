package aguDataSystem.server.domain

/**
 * Represents the Autonomous Gas Unit (AGU) in the system.
 *
 * @property name name of the AGU
 * @property cui CUI of the AGU
 * @property levels gas levels of the AGU
 * @property loadVolume load volume of the AGU
 * @property location location of the AGU
 * @property dno DNO of the AGU
 * @property isFavorite whether the AGU is a favorite
 * @property notes notes of the AGU
 * @property training training of the AGU
 * @property image image of the AGU
 * @property contacts contacts of the AGU
 * @property tanks tanks of the AGU
 * @property providers providers of the AGU
 */
data class AGU(
	val cui: String,
	val name: String,
	val levels: GasLevels,
	val loadVolume: Int,
	val location: Location,
	val dno: DNO,
	val isFavorite: Boolean = false,
	val notes: String? = null,
	val training: String,
	val image: ByteArray, //TODO: change later to an Image object
	val contacts: List<Contact>,
	val tanks: List<Tank>,
	val providers: List<Provider>
) {

	// calculate the total capacity of the AGU
	val capacity = tanks.sumOf { it.capacity }

	init {
		require(AGUDomain().isCUIValid(cui)) { "CUI is not valid" }
		require(AGUDomain().isTanksValid(tanks)) { "There must be at least one tank" }
		require(AGUDomain().isLatitudeValid(location.latitude)) { "Latitude is not valid" }
		require(AGUDomain().isLongitudeValid(location.longitude)) { "Longitude is not valid" }
		require(AGUDomain().isPercentageValid(loadVolume)) { "Load volume is not valid" }
	}
}