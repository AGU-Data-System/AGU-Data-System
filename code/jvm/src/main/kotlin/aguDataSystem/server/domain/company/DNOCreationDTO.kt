package aguDataSystem.server.domain.company

/**
 * Represents the input model for creating a new [DNO]
 *
 * @property name The name of the [DNO]
 * @property region The region of the [DNO]
 */
data class DNOCreationDTO(
	val name: String,
	val region: String
)