package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.contact.ContactCreationDTO
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.tank.Tank

/**
 * Data Transfer Object for creating an AGU
 *
 * @property cui CUI of the AGU
 * @property eic EIC of the AGU
 * @property name name of the AGU
 * @property levels gas levels of the AGU
 * @property loadVolume load volume of the AGU
 * @property correctionFactor correction factor of the AGU
 * @property location location of the AGU
 * @property dnoName DNO name associated with the AGU
 * @property gasLevelUrl URL of the gas level
 * @property image image of the AGU
 * @property contacts contacts of the AGU
 * @property tanks tanks of the AGU
 * @property transportCompanies transport companies that this AGU is associated with
 * @property isFavourite whether the AGU is a favorite
 * @property isActive whether the AGU is active
 * @property notes notes of the AGU
 * @property training training of the AGU
 */
data class AGUCreationDTO(
	val cui: String,
	val eic: String,
	val name: String,
	val levels: GasLevels,
	val loadVolume: Int,
	val correctionFactor: Double,
	val location: Location,
	val dnoName: String,
	val gasLevelUrl: String,
	val image: ByteArray? = null,
	val contacts: List<ContactCreationDTO>,
	val tanks: List<Tank>,
	val transportCompanies: List<String>,
	val isFavourite: Boolean = false,
	val isActive: Boolean = true,
	val notes: String? = null,
	val training: String? = null,
) {

	/**
	 * Converts the creation DTO to AGU creation info
	 *
	 * @receiver the creation DTO
	 * @return the AGU
	 */
	fun toAGUCreationInfo() = AGUCreationInfo(
		cui = this.cui,
		eic = this.eic,
		name = this.name,
		levels = this.levels,
		loadVolume = this.loadVolume,
		correctionFactor = this.correctionFactor,
		location = this.location,
		dnoName = this.dnoName,
		gasLevelUrl = this.gasLevelUrl,
		image = this.image,
		contacts = this.contacts.map { it.toContactCreation() },
		tanks = this.tanks,
		transportCompanies = this.transportCompanies,
		isFavourite = this.isFavourite,
		isActive = this.isActive,
		notes = this.notes,
		training = this.training
	)
}