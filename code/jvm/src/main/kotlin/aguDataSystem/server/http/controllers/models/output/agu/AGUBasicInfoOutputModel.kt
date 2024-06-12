package aguDataSystem.server.http.controllers.models.output.agu

import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.http.controllers.models.output.dno.DNOOutputModel
import aguDataSystem.server.http.controllers.models.output.location.LocationOutputModel
import aguDataSystem.server.http.controllers.models.output.transportCompany.TransportCompanyOutputModel

/**
 * Output model for AGU basic info
 *
 * @param cui The CUI of the AGU
 * @param name The name of the AGU
 * @param dno The DNO of the AGU
 * @param location The location of the AGU
 */
data class AGUBasicInfoOutputModel(
	val cui: String,
	val eic: String,
	val name: String,
	val isFavorite: Boolean,
	val dno: DNOOutputModel,
	val location: LocationOutputModel,
	val transportCompanies: List<TransportCompanyOutputModel>
) {
	constructor(agu: AGUBasicInfo) : this(
		cui = agu.cui,
		eic = agu.eic,
		name = agu.name,
		isFavorite = agu.isFavorite,
		dno = DNOOutputModel(agu.dno),
		location = LocationOutputModel(agu.location),
		transportCompanies = agu.transportCompanies.map { TransportCompanyOutputModel(it) }
	)
}
