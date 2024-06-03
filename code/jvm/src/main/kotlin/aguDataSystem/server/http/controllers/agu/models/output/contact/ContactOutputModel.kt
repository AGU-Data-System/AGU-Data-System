package aguDataSystem.server.http.controllers.agu.models.output.contact

import aguDataSystem.server.domain.contact.Contact

/**
 * Represents the output model for a [Contact]
 *
 * @property id The id of the [Contact]
 * @property name The name of the [Contact]
 * @property phone The phone number of the [Contact]
 * @property type The type of the [Contact]
 */
data class ContactOutputModel(
	val id: Int,
	val name: String,
	val phone: String,
	val type: String
) {
	constructor(contact: Contact) : this(
		id = contact.id,
		name = contact.name,
		phone = contact.phone,
		type = contact.type.toString()
	)
}