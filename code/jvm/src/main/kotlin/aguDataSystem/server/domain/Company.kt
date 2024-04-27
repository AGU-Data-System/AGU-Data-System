package aguDataSystem.server.domain

/**
 * Represents the Transport Companies
 *
 * @property id the id of the Transport Company
 * @property name the name of the Transport Company
 */
data class TransportCompany(
	val id: Int,
	val name: String
)

/**
 * Represents the Distribution Network Operators
 *
 *
 * @property id the id of the Operator
 * @property name the name of the Operator
 */
data class DNO(
	val id: Int,
	val name: String
)
