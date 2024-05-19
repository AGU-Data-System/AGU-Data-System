package aguDataSystem.server.domain.contact

/**
 * Data Transfer Object for Creating a Contact
 *
 * @property name name of the contact
 * @property phone phone number of the contact
 * @property type type of the contact
 */
data class ContactCreationDTO(
	val name: String,
	val phone: String,
	val type: String
) {

	/**
	 * Converts this ContactDTO to a Contact
	 *
	 * @return the Contact
	 */
	fun toContactCreation() = ContactCreation(
		name = this.name,
		phone = this.phone,
		type = this.type.toContactType()
	)
}