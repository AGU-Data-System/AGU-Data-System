package aguDataSystem.server.domain.contact

/**
 * Represents the needed information to create a contact
 *
 * @property name name of the contact
 * @property phone phone number of the contact
 * @property type type of the contact
 */
data class ContactCreation(
	val name: String,
	val phone: String,
	val type: ContactType
)