package aguDataSystem.server.repository.contact

import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.ContactCreation

/**
 * Interface for the Contact repository
 */
interface ContactRepository {

	/**
	 * Adds a contact to an AGU
	 *
	 * @param cui CUI of the AGU
	 * @param contact Contact to add
	 * @return ID of the contact
	 */
	fun addContact(cui: String, contact: ContactCreation): Int

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
	 * @param id ID of the contact
	 */
	fun deleteContact(cui: String, id: Int)

	/**
	 * Checks whether a contact is stored
	 *
	 * @param cui CUI of the AGU
	 * @param id ID of the contact
	 * @return True if contact is stored, false otherwise
	 */
	fun isContactStoredById(cui: String, id: Int): Boolean

	/**
	 * Checks whether a contact is stored by phone number and type
	 *
	 * @param cui CUI of the AGU
	 * @param phoneNumber Phone number of the contact
	 * @param type Type of the contact
	 * @return True if contact is stored, false otherwise
	 */
	fun isContactStoredByPhoneNumberAndType(cui: String, phoneNumber: String, type: String): Boolean
}