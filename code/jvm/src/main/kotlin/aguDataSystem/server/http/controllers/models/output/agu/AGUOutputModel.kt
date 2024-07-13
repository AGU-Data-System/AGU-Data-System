package aguDataSystem.server.http.controllers.models.output.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.http.controllers.models.output.contact.ContactListOutputModel
import aguDataSystem.server.http.controllers.models.output.dno.DNOOutputModel
import aguDataSystem.server.http.controllers.models.output.gasLevels.GasLevelsOutputModel
import aguDataSystem.server.http.controllers.models.output.location.LocationOutputModel
import aguDataSystem.server.http.controllers.models.output.provider.ProviderListOutputModel
import aguDataSystem.server.http.controllers.models.output.tank.TankListOutputModel
import aguDataSystem.server.http.controllers.models.output.transportCompany.TransportCompanyListOutputModel

/**
 * Output model for AGU
 *
 * @param cui The CUI of the AGU
 * @param eic The EIC of the AGU
 * @param name The name of the AGU
 * @param levels The gas levels of the AGU
 * @param loadVolume The load volume of the AGU
 * @param correctionFactor The correction factor of the AGU
 * @param location The location of the AGU
 * @param dno The DNO of the AGU
 * @param image The image of the AGU
 * @param contacts The contacts of the AGU
 * @param tanks The tanks of the AGU
 * @param providers The providers of the AGU
 * @param transportCompanies The transport companies of the AGU
 * @param isFavourite The favorite status of the AGU
 * @param isActive The active status of the AGU
 * @param notes The notes of the AGU
 * @param training The training of the AGU
 * @param capacity The capacity of the AGU
 */
data class AGUOutputModel(
	val cui: String,
	val eic: String,
	val name: String,
	val levels: GasLevelsOutputModel,
	val loadVolume: Int,
	val correctionFactor: Double,
	val location: LocationOutputModel,
	val dno: DNOOutputModel,
	val image: ByteArray,
	val contacts: ContactListOutputModel,
	val tanks: TankListOutputModel,
	val providers: ProviderListOutputModel,
	val transportCompanies: TransportCompanyListOutputModel,
	val isFavourite: Boolean = false,
	val isActive: Boolean = true,
	val notes: String? = null,
	val training: String?,
	val capacity: Int = 0,
) {
	constructor(agu: AGU) : this(
		cui = agu.cui,
		eic = agu.eic,
		name = agu.name,
		levels = GasLevelsOutputModel(agu.levels),
		loadVolume = agu.loadVolume,
		correctionFactor = agu.correctionFactor,
		location = LocationOutputModel(agu.location),
		dno = DNOOutputModel(agu.dno),
		image = agu.image,
		contacts = ContactListOutputModel(agu.contacts),
		tanks = TankListOutputModel(agu.tanks),
		providers = ProviderListOutputModel(agu.providers),
		transportCompanies = TransportCompanyListOutputModel(agu.transportCompanies),
		isFavourite = agu.isFavourite,
		isActive = agu.isActive,
		notes = agu.notes,
		training = agu.training,
		capacity = agu.capacity
	)
}