package aguDataSystem.server.domain.company

/**
 * Represents a Company
 *
 * @property id the id of the Company
 * @property name the name of the Company
 */
sealed class Company {
	abstract val id: Int
	abstract val name: String
}
