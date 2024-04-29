package aguDataSystem.server.domain

/**
 * Represents a Company
 */
sealed class Company {
	abstract val id: Int
	abstract val name: String
}

/**
 * Represents the Transport Companies
 *
 * @property id the id of the Transport Company
 * @property name the name of the Transport Company
 */
data class TransportCompany(
	override val id: Int,
	override val name: String
) : Company()


/**
 * Represents the Distribution Network Operators
 *
 *
 * @property id the id of the Operator
 * @property name the name of the Operator
 */
data class DNO(
	override val id: Int,
	override val name: String
) : Company()
