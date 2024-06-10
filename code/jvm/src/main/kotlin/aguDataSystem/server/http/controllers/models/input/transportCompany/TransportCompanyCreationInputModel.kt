package aguDataSystem.server.http.controllers.models.input.transportCompany

import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.domain.company.TransportCompanyCreationDTO

/**
 * Represents the input model for creating a [TransportCompany]
 *
 * @property name The name of the [TransportCompany]
 */
data class TransportCompanyCreationInputModel(
	val name: String
) {
	/**
	 * Converts the input model to a data transfer object
	 *
	 * @receiver the TransportCompany input model
	 * @return the TransportCompany data transfer object
	 */
	fun toTransportCompanyCreationDTO() = TransportCompanyCreationDTO(
		name = this.name
	)
}
