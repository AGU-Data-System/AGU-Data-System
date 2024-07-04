package aguDataSystem.server.http.controllers.models.output.agu

import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.http.controllers.models.output.dno.DNOOutputModel
import aguDataSystem.server.http.controllers.models.output.location.LocationOutputModel
import aguDataSystem.server.http.controllers.models.output.transportCompany.TransportCompanyListOutputModel

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
	val isFavourite: Boolean,
	val dno: DNOOutputModel,
	val location: LocationOutputModel,
	val transportCompanies: TransportCompanyListOutputModel
) {
	constructor(agu: AGUBasicInfo) : this(
		cui = agu.cui,
		eic = agu.eic,
		name = agu.name,
		isFavourite = agu.isFavourite,
		dno = DNOOutputModel(agu.dno),
		location = LocationOutputModel(agu.location),
		transportCompanies = TransportCompanyListOutputModel(agu.transportCompanies)
	)
}
