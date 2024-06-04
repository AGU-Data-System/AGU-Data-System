package aguDataSystem.server.http.controllers.agu.models.output.dno

import aguDataSystem.server.domain.company.DNO

/**
 * Output model for DNO
 *
 * @param id The id of the DNO
 * @param name The name of the DNO
 */
data class DNOOutputModel(
	val id: Int,
	val name: String,
	val region: String
) {
	constructor(dno: DNO) : this(
		id = dno.id,
		name = dno.name,
		region = dno.region
	)
}
