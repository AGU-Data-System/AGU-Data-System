package aguDataSystem.server.domain

/**
 * Represents a contact.
 *
 * @property name The name of the contact.
 * @property phone The phone number of the contact.
 */
sealed class Contact {
	abstract val name: String
	abstract val phone: String
}

/**
 * Represents a logistic contact.
 *
 * @property name The name of the contact.
 * @property phone The phone number of the contact.
 */
class Logistic(
	override val name: String,
	override val phone: String,
): Contact()

/**
 * Represents an emergency contact.
 *
 * @property name The name of the contact.
 * @property phone The phone number of the contact.
 */
class Emergency(
	override val name: String,
	override val phone: String,
): Contact()

/**
 * Creates a contact based on a type.
 *
 * @param name The name of the contact.
 * @param phone The phone number of the contact.
 * @return The created contact.
 * @throws IllegalArgumentException If the type is invalid.
 */
fun createContact(name: String, phone: String, type: String): Contact {
	return when(type) {
		"LOGISTIC" -> Logistic(name, phone)
		"EMERGENCY" -> Emergency(name, phone)
		else -> throw IllegalArgumentException("Invalid contact type")
	}
}