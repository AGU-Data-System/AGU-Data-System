package aguDataSystem.server.domain

/**
 * Represents a contact
 *
 * @property name name of the contact
 * @property phone phone number of the contact
 * @property type type of the contact
 */
data class Contact(
	val name: String,
	val phone: String,
	val type: String
)