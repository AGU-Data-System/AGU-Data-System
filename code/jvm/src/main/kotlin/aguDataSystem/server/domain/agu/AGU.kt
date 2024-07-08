package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.tank.Tank

/**
 * Represents an Autonomous Gas Unit (AGU) in the system.
 *
 * @property cui CUI of the AGU
 * @property eic EIC of the AGU
 * @property name name of the AGU
 * @property levels gas levels of the AGU
 * @property loadVolume amount that a load fills the agu.
 * @property capacity capacity of the AGU based on the [Tank]s
 * @property correctionFactor correction factor of the AGU.
 * @property location location of the AGU
 * @property dno DNO of the AGU
 * @property image image of the AGU
 * @property contacts contacts of the AGU
 * @property tanks tanks of the AGU
 * @property providers providers of the AGU
 * @property transportCompanies transport companies that this AGU is associated with
 * @property isFavourite whether the AGU is a favorite
 * @property isActive whether the AGU is active
 * @property notes notes of the AGU
 * @property training training of the AGU
 */
data class AGU(
	val cui: String,
	val eic: String,
	val name: String,
	val levels: GasLevels,
	val loadVolume: Int,
	val correctionFactor: Double,
	val location: Location,
	val dno: DNO,
	val image: ByteArray,
	val contacts: List<Contact>,
	val tanks: List<Tank>,
	val providers: List<Provider>,
	val transportCompanies: List<TransportCompany>,
	val isFavourite: Boolean = false,
	val isActive: Boolean = true,
	val notes: String? = null,
	val training: String?,
) {

	// calculate the total capacity of the AGU
	val capacity = tanks.sumOf { it.capacity }
}
