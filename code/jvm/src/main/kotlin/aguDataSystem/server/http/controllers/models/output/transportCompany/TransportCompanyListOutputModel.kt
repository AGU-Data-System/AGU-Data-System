package aguDataSystem.server.http.controllers.models.output.transportCompany

import aguDataSystem.server.domain.company.TransportCompany

/**
 * Output model for a list of transport companies
 *
 * @param transportCompanies The list of transport companies
 * @param size The size of the list
 */
data class TransportCompanyListOutputModel(
	val transportCompanies: List<TransportCompanyOutputModel>,
	val size: Int
) {
	constructor(transportCompanies: List<TransportCompany>) : this(
		transportCompanies = transportCompanies.map { TransportCompanyOutputModel(it) },
		size = transportCompanies.size
	)
}