package aguDataSystem.server.domain.company

/**
 * Represents a Distribution Network Operator
 *
 * @property id the id of the Operator
 * @property name the name of the Operator
 */
data class DNO(
	override val id: Int,
	override val name: String
) : Company()
