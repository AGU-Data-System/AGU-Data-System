package aguDataSystem.server.domain.contact

enum class ContactType {
	EMERGENCY,
	LOGISTIC;

	/**
	 * Creates a contact based on a type
	 *
	 * @receiver The type of the contact
	 * @param name The name of the contact
	 * @param phone The phone number of the contact
	 * @return The created contact
	 * @throws IllegalArgumentException If the type is invalid
	 */
	fun createContact(name: String, phone: String): Contact {
		return when (this) {
			LOGISTIC -> LogisticContact(name, phone)
			EMERGENCY -> EmergencyContact(name, phone)
		}
	}
}