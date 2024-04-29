package aguDataSystem.server.domain.contact

/**
 * Represents a contact
 *
 * @property name The name of the contact
 * @property phone The phone number of the contact
 */
sealed class Contact {
	abstract val name: String
	abstract val phone: String
}
