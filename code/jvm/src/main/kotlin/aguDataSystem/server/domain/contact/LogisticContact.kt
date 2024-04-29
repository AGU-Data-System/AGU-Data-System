package aguDataSystem.server.domain.contact

/**
 * Represents a logistic contact
 *
 * @property name The name of the contact
 * @property phone The phone number of the contact
 */
class LogisticContact(
	override val name: String,
	override val phone: String,
) : Contact()
