package aguDataSystem.server.repository.contact

import aguDataSystem.server.domain.Contact

interface ContactRepository {
    fun addContact(cui: String, contact: Contact)
}