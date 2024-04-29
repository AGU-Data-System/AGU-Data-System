package aguDataSystem.server.domain.contact

/**
 * Represents an emergency contact
 *
 * @property name The name of the contact
 * @property phone The phone number of the contact
 */
class EmergencyContact(
	override val name: String,
	override val phone: String,
) : Contact()