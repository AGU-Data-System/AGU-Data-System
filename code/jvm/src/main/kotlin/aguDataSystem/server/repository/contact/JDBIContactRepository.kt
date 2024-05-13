package aguDataSystem.server.repository.contact

import aguDataSystem.server.domain.contact.Contact
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
	override fun addContact(cui: String, contact: Contact) {

		logger.info("Adding contact with type {}, to AGU with CUI {}", contact.type, cui)

		handle.createUpdate(
			"""
                INSERT INTO contacts (name, phone, type, agu_cui)
                VALUES (:name, :phone, :type, :agu_cui)
            """.trimIndent()
		)
			.bind("name", contact.name)
			.bind("phone", contact.phone)
			.bind("type", contact.type)
			.bind("agu_cui", cui)
			.execute()

		logger.info("Added contact with type {}, to AGU with CUI {}", contact.type, cui)
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
	 * @param phone Phone of the contact
	 */
	override fun deleteContact(cui: String, phone: String) {
		logger.info("Attempting to delete contact with phone {} from AGU with CUI {}", phone, cui)

		val deletions = handle.createUpdate(
			"""
                DELETE FROM contacts
                WHERE agu_cui = :cui AND phone = :phone
            """.trimIndent()
		)
			.bind("cui", cui)
			.bind("phone", phone)
			.execute()

		logger.info("Deleted {} contacts with phone {} from AGU with CUI {}", deletions, phone, cui)
	}

	/**
	 * Checks whether a contact is stored
	 *
	 * @param cui CUI of the AGU
	 * @param contact Contact to check
	 * @return True if contact is stored, false otherwise
	 */
	override fun isContactStored(cui: String, contact: Contact): Boolean {
		logger.info("Checking if contact {} is stored for AGU with CUI {}", contact, cui)

		val isStored = handle.createQuery(
			"""
                SELECT COUNT(*)
                FROM contacts
                WHERE agu_cui = :cui AND phone = :phone AND type = :type AND name = :name
            """.trimIndent()
		)
			.bind("cui", cui)
			.bind("phone", contact.phone)
			.bind("type", contact.type)
			.bind("name", contact.name)
			.mapTo<Int>()
			.single() == 1

		logger.info("Contact is ${if (!isStored) "not" else ""} stored for AGU with CUI: {}", cui)

		return isStored
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIContactRepository::class.java)
	}
}
