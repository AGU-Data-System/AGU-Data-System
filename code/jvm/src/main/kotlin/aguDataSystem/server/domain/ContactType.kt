package aguDataSystem.server.domain

/**
 * Enum class for the type of contact
 */
enum class ContactType {
	EMERGENCY, LOGISTIC
}

/**
 * Transform a string to a ContactType
 */
fun String.toContactType(): ContactType {
	return when (this) {
		"EMERGENCY" -> ContactType.EMERGENCY
		"LOGISTIC" -> ContactType.LOGISTIC
		else -> throw IllegalArgumentException("Invalid contact type")
	}
}