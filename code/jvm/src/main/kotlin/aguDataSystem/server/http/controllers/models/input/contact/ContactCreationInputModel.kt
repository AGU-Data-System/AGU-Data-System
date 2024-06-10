package aguDataSystem.server.http.controllers.models.input.contact

import aguDataSystem.server.domain.contact.ContactCreationDTO

/**
 * The input model for creating a contact
 *
 * @param name the name of the contact
 * @param phone the phone of the contact
 * @param type the type of the contact
 */
data class ContactCreationInputModel(
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
	fun toContactCreationDTO() = ContactCreationDTO(
		name = this.name,
		phone = this.phone,
		type = this.type
	)
}
