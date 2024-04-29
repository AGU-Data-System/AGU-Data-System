package aguDataSystem.server.domain.company

/**
 * Represents a Transport Company
 *
 * @property id the id of the Transport Company
 * @property name the name of the Transport Company
 */
data class TransportCompany(
	override val id: Int,
	override val name: String
) : Company()
