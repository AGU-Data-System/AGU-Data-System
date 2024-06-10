package aguDataSystem.server.http.controllers.models.output.transportCompany

import aguDataSystem.server.domain.company.TransportCompany

/**
 * Output model for a transport company
 *
 * @property id The id of the transport company
 * @property name The name of the transport company
 */
data class TransportCompanyOutputModel(
	val id: Int,
	val name: String
) {
	constructor(transportCompany: TransportCompany) : this(
		id = transportCompany.id,
		name = transportCompany.name
	)
}