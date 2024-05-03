package aguDataSystem.server.repository.contact

import aguDataSystem.server.domain.contact.Contact
import org.jdbi.v3.core.Handle
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [ContactRepository]
 * @see ContactRepository
 * @see Handle
 */
class JDBIContactRepository(private val handle: Handle): ContactRepository {

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

    companion object {
        private val logger = LoggerFactory.getLogger(JDBIContactRepository::class.java)
    }
}
