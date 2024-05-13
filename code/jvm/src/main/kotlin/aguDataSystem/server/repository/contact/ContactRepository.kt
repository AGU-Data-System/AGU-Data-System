package aguDataSystem.server.repository.contact

import aguDataSystem.server.domain.contact.Contact

/**
 * Interface for the Contact repository
 */
interface ContactRepository {

	/**
	 * Adds a contact to an AGU
	 *
	 * @param cui CUI of the AGU
	 * @param contact Contact to add
	 */
	fun addContact(cui: String, contact: Contact)

	/**
	 * Gets all contacts of an AGU
	 *
	 * @param cui CUI of the AGU
	 * @return List of contacts
	 */
	fun getContactsByAGU(cui: String): List<Contact>

	/**
	 * Deletes a contact from an AGU
	 *
	 * @param cui CUI of the AGU
	 * @param phone Phone of the contact
	 */
	fun deleteContact(cui: String, phone: String)

	/**
	 * Checks whether a contact is stored
	 *
	 * @param cui CUI of the AGU
	 * @param contact Contact to check
	 * @return True if contact is stored, false otherwise
	 */
	fun isContactStored(cui: String, contact: Contact): Boolean
}