package aguDataSystem.server.domain.company

/**
 * Represents a DTO model for creating a new [TransportCompany]
 *
 * @property name The name of the Transport Company
 */
data class TransportCompanyCreationDTO(
	val name: String
)