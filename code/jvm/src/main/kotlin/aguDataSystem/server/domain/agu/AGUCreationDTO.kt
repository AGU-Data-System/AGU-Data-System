package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.company.DNOCreationDTO
import aguDataSystem.server.domain.contact.ContactCreationDTO
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.tank.Tank

/**
 * Data Transfer Object for creating an AGU
 *
 * @property cui CUI of the AGU
 * @property name name of the AGU
 * @property levels gas levels of the AGU
 * @property loadVolume load volume of the AGU
 * @property location location of the AGU
 * @property dno DNO of the AGU
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
	val dno: DNOCreationDTO,
	val gasLevelUrl: String,
	val image: ByteArray, //TODO: change later to an Image object
	val contacts: List<ContactCreationDTO>,
	val tanks: List<Tank>,
	val isFavorite: Boolean = false,
	val notes: String?,
	val training: String?,
) {

	/**
	 * Converts the creation DTO to AGU creation info
	 *
	 * @receiver the creation DTO
	 * @return the AGU
	 */
	fun toAGUCreationInfo() = AGUCreationInfo(
		cui = this.cui,
		name = this.name,
		levels = this.levels,
		loadVolume = this.loadVolume,
		location = this.location,
		dno = this.dno,
		gasLevelUrl = this.gasLevelUrl,
		image = this.image,
		contacts = this.contacts.map { it.toContactCreation() },
		tanks = this.tanks,
		isFavorite = this.isFavorite,
		notes = this.notes,
		training = this.training
	)
}