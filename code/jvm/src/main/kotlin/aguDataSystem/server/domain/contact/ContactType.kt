package aguDataSystem.server.domain.contact

/**
 * Represents a contact type, either emergency or logistic
 *
 */
enum class ContactType {
	EMERGENCY,
	LOGISTIC
}

/**
 * Converts a string to a contact type
 *
 * @receiver The string to convert
 * @return The contact type
 * @throws IllegalArgumentException If the string is not a valid contact type
 */
fun String.toContactType(): ContactType = ContactType.valueOf(this.uppercase())
