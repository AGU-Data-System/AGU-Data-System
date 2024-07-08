package aguDataSystem.server.http.controllers.models.input.agu

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.http.controllers.models.input.contact.ContactCreationInputModel
import aguDataSystem.server.http.controllers.models.input.tank.TankCreationInputModel

/**
 * The input model for creating an AGU
 *
 * @param cui the CUI of the AGU
 * @param eic the EIC of the AGU
 * @param name the name of the AGU
 * @param minLevel the minimum level of the AGU
 * @param maxLevel the maximum level of the AGU
 * @param criticalLevel the critical level of the AGU
 * @param loadVolume the load volume of the AGU
 * @param correctionFactor the correction factor of the AGU
 * @param latitude the latitude of the AGU
 * @param longitude the longitude of the AGU
 * @param locationName the name of the location of the AGU
 * @param dnoName the DNO name that the AGU is associated with
 * @param gasLevelUrl the URL of the gas level of the AGU
 * @param image the image of the AGU
 * @param tanks the tanks of the AGU
 * @param contacts the contacts of the AGU
 * @param isFavourite the favorite status of the AGU
 * @param isActive the active status of the AGU
 * @param notes the notes of the AGU
 */
data class AGUCreationInputModel(
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
	val image: ByteArray? = null,
	val tanks: List<TankCreationInputModel>,
	val contacts: List<ContactCreationInputModel>,
	val transportCompanies: List<String>,
	val isFavourite: Boolean = false,
	val isActive: Boolean = true,
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
		eic = this.eic,
		name = this.name,
		levels = GasLevels(
			min = this.minLevel,
			max = this.maxLevel,
			critical = this.criticalLevel
		),
		loadVolume = this.loadVolume,
		correctionFactor = this.correctionFactor,
		location = Location(
			name = this.locationName,
			latitude = this.latitude,
			longitude = this.longitude
		),
		dnoName = this.dnoName,
		gasLevelUrl = this.gasLevelUrl,
		image = this.image,
		contacts = this.contacts.map { contactCreationInputModel -> contactCreationInputModel.toContactCreationDTO() },
		tanks = this.tanks.map { tankInputModel -> tankInputModel.toTank() },
		transportCompanies = this.transportCompanies,
		isFavourite = this.isFavourite,
		isActive = this.isActive,
		notes = this.notes,
		training = null
	)
}
