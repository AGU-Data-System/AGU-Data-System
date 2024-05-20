package aguDataSystem.server.repository.contact

import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.ContactCreation
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [ContactRepository]
 * @see ContactRepository
 * @see Handle
 */
class JDBIContactRepository(private val handle: Handle) : ContactRepository {

	/**
	 * Adds a contact to an AGU
	 *
	 * @param cui CUI of the AGU
	 * @param contact Contact to add
	 */
	override fun addContact(cui: String, contact: ContactCreation): Int {

		logger.info("Adding contact with type {}, to AGU with CUI {}", contact.type, cui)

		val contactId = handle.createUpdate(
			"""
                INSERT INTO contacts (name, phone, type, agu_cui)
                VALUES (:name, :phone, :type, :agu_cui)
            """.trimIndent()
		)
			.bind("name", contact.name)
			.bind("phone", contact.phone)
			.bind("type", contact.type)
			.bind("agu_cui", cui)
			.executeAndReturnGeneratedKeys(Contact::id.name)
			.mapTo<Int>()
			.one()

		logger.info("Added contact with type {} and id {}, to AGU with CUI {}", contact.type, contactId, cui)

		return contactId
	}

	/**
	 * Gets all contacts of an AGU
	 *
	 * @param cui CUI of the AGU
	 * @return List of contacts
	 */
	override fun getContactsByAGU(cui: String): List<Contact> {
		logger.info("Getting contacts of AGU with CUI {}", cui)

		val contacts = handle.createQuery(
			"""
                SELECT *
                FROM contacts
                WHERE agu_cui = :cui
            """.trimIndent()
		)
			.bind("cui", cui)
			.mapTo<Contact>()
			.list()

		logger.info("Retrieved {} contacts of AGU with CUI {}", contacts.size, cui)

		return contacts
	}

	/**
	 * Deletes a contact from an AGU
	 *
	 * @param cui CUI of the AGU
	 * @param id ID of the contact
	 */
	override fun deleteContact(cui: String, id: Int) {
		logger.info("Attempting to delete contact with id {} from AGU with CUI {}", id, cui)

		val deletions = handle.createUpdate(
			"""
                DELETE FROM contacts
                WHERE agu_cui = :cui AND id = :id
            """.trimIndent()
		)
			.bind("cui", cui)
			.bind("id", id)
			.execute()

		logger.info("Deleted {} contacts with id {} from AGU with CUI {}", deletions, id, cui)
	}

	/**
	 * Checks whether a contact is stored
	 *
	 * @param cui CUI of the AGU
	 * @param id ID of the contact
	 * @return True if contact is stored, false otherwise
	 */
	override fun isContactStoredById(cui: String, id: Int): Boolean {
		logger.info("Checking if contact with ID {} is stored for AGU with CUI {}", id, cui)

		val isStored = handle.createQuery(
			"""
				SELECT COUNT(*)
				FROM contacts
				WHERE agu_cui = :cui AND id = :id
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("id", id)
			.mapTo<Int>()
			.single() == 1

		logger.info("Contact is ${if (!isStored) "not" else ""} stored for AGU with CUI: {}", cui)

		return isStored
	}

	/**
	 * Checks whether a contact is stored by phone number and type
	 *
	 * @param cui CUI of the AGU
	 * @param phoneNumber Phone number of the contact
	 * @param type Type of the contact
	 * @return True if contact is stored, false otherwise
	 */
	override fun isContactStoredByPhoneNumberAndType(cui: String, phoneNumber: String, type: String): Boolean {
		logger.info(
			"Checking if contact with phone {} and type {} is stored for AGU with CUI {}",
			phoneNumber,
			type,
			cui
		)

		val isStored = handle.createQuery(
			"""
				SELECT COUNT(*)
				FROM contacts
				WHERE agu_cui = :cui AND phone = :phone AND type = :type
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("phone", phoneNumber)
			.bind("type", type)
			.mapTo<Int>()
			.single() == 1

		logger.info("Contact is ${if (!isStored) "not" else ""} stored for AGU with CUI: {}", cui)

		return isStored
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIContactRepository::class.java)
	}
}
