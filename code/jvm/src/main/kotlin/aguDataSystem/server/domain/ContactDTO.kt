package aguDataSystem.server.domain

/**
 * Data Transfer Object for creating a contact
 *
 * @property name name of the contact
 * @property phone phone number of the contact
 * @property type type of the contact
 */
data class ContactDTO(
	val name: String,
	val phone: String,
	val type: String
)