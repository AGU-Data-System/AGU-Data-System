package aguDataSystem.server.http.controllers.agu.models.addAgu

import aguDataSystem.server.domain.contact.ContactDTO

/**
 * The input model for creating a contact
 *
 * @param name the name of the contact
 * @param phone the phone of the contact
 * @param type the type of the contact
 */
data class ContactInputModel(
	val name: String,
	val phone: String,
	val type: String
) {
	/**
	 * Converts the input model to a data transfer object
	 *
	 * @receiver the contact input model
	 * @return the contact data transfer object
	 */
	fun toContactDTO() = ContactDTO(
		name = this.name,
		phone = this.phone,
		type = this.type
	)
}
