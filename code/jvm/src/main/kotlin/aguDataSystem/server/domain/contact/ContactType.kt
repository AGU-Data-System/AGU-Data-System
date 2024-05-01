package aguDataSystem.server.domain.contact

/**
 * Represents a contact type, either emergency or logistic
 *
 */
enum class ContactType {
	EMERGENCY,
	LOGISTIC
}

fun String.toContactType(): ContactType {
	return when (this.uppercase()) {
		"EMERGENCY" -> ContactType.EMERGENCY
		"LOGISTIC" -> ContactType.LOGISTIC
		else -> throw IllegalArgumentException("Invalid contact type")
	}
}