package aguDataSystem.server.http.controllers.agu.models.addAgu

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGUCreationDTO

/**
 * The input model for creating an AGU
 *
 * @param cui the CUI of the AGU
 * @param name the name of the AGU
 * @param minLevel the minimum level of the AGU
 * @param maxLevel the maximum level of the AGU
 * @param criticalLevel the critical level of the AGU
 * @param loadVolume the load volume of the AGU
 * @param latitude the latitude of the AGU
 * @param longitude the longitude of the AGU
 * @param locationName the name of the location of the AGU
 * @param dnoName the name of the DNO of the AGU
 * @param gasLevelUrl the URL of the gas level of the AGU
 * @param image the image of the AGU
 * @param tanks the tanks of the AGU
 * @param contacts the contacts of the AGU
 * @param isFavorite the favorite status of the AGU
 * @param notes the notes of the AGU
 */
data class AGUCreationInputModel(
	val cui: String,
	val name: String,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val loadVolume: Double,
	val latitude: Double,
	val longitude: Double,
	val locationName: String,
	val dnoName: String,
	val gasLevelUrl: String,
	val image: ByteArray,
	val tanks: List<TankInputModel>,
	val contacts: List<ContactInputModel>,
	val isFavorite: Boolean = false,
	val notes: String? = null,
) {

	/**
	 * Converts the input model to a data transfer object
	 *
	 * @receiver the AGU creation input model
	 * @return the AGU creation data transfer object
	 */
	fun toAGUCreationDTO() = AGUCreationDTO(
		cui = this.cui,
		name = this.name,
		levels = GasLevels(
			min = this.minLevel,
			max = this.maxLevel,
			critical = this.criticalLevel
		),
		loadVolume = this.loadVolume.toInt(),
		location = Location(
			name = this.locationName,
			latitude = this.latitude,
			longitude = this.longitude
		),
		dnoName = this.dnoName,
		gasLevelUrl = this.gasLevelUrl,
		image = this.image,
		contacts = this.contacts.map { contactInputModel -> contactInputModel.toContactDTO() },
		tanks = this.tanks.map { tankInputModel -> tankInputModel.toTankDTO() },
		isFavorite = this.isFavorite,
		notes = this.notes,
		training = null
	)
}
