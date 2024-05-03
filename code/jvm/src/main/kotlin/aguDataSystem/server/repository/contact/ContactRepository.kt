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
}