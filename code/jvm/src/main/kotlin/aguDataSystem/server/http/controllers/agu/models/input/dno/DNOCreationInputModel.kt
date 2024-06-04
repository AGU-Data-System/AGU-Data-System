package aguDataSystem.server.http.controllers.agu.models.input.dno

import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.company.DNOCreationDTO

/**
 * Represents the input model for creating a new [DNO]
 *
 * @property name The name of the [DNO]
 * @property region The region of the [DNO]
 */
data class DNOCreationInputModel(
	val name: String,
	val region: String
) {

	/**
	 * Converts the input model to a data transfer object
	 *
	 * @receiver the DNO input model
	 * @return the DNO data transfer object
	 */
	fun toDNOCreationDTO() = DNOCreationDTO(
		name = this.name,
		region = this.region
	)
}