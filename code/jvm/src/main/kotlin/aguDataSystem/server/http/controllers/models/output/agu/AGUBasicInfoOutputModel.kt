package aguDataSystem.server.http.controllers.models.output.agu

import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.http.controllers.models.output.dno.DNOOutputModel
import aguDataSystem.server.http.controllers.models.output.location.LocationOutputModel

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
	val name: String,
	val dno: DNOOutputModel,
	val location: LocationOutputModel,
) {
	constructor(agu: AGUBasicInfo) : this(
		cui = agu.cui,
		name = agu.name,
		dno = DNOOutputModel(agu.dno),
		location = LocationOutputModel(agu.location)
	)
}
