package aguDataSystem.server.http.controllers.agu.models.output.contact

import aguDataSystem.server.domain.contact.Contact

/**
 * Represents the output model for a list of [Contact]s
 *
 * @property contacts The list of [Contact]s
 * @property size The size of the list
 */
data class ContactListOutputModel(
	val contacts: List<ContactOutputModel>,
	val size: Int
) {
	constructor(contacts: List<Contact>) : this(
		contacts = contacts.map { ContactOutputModel(it) },
		size = contacts.size
	)
}
