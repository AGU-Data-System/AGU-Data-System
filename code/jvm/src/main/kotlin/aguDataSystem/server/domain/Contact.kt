package aguDataSystem.server.domain

/**
 * Represents a contact.
 *
 * @property name The name of the contact.
 * @property phone The phone number of the contact.
 * @property type The type of the contact.
 */
class Contact(
	val name: String,
	val phone: String,
	val type: ContactType
)