package aguDataSystem.server.http.controllers.agu.models.input.agu

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.http.controllers.agu.models.input.contact.ContactCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.dno.DNOCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.tank.TankCreationInputModel

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
 * @param dnoCreation the DNO creation input model
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
	val dnoCreation: DNOCreationInputModel,
	val gasLevelUrl: String,
	val image: ByteArray,
	val tanks: List<TankCreationInputModel>,
	val contacts: List<ContactCreationInputModel>,
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
		dno = this.dnoCreation.toDNOCreationDTO(),
		gasLevelUrl = this.gasLevelUrl,
		image = this.image,
		contacts = this.contacts.map { contactCreationInputModel -> contactCreationInputModel.toContactCreationDTO() },
		tanks = this.tanks.map { tankInputModel -> tankInputModel.toTank() },
		isFavorite = this.isFavorite,
		notes = this.notes,
		training = null
	)
}
