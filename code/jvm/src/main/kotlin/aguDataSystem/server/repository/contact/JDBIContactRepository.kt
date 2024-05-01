package aguDataSystem.server.repository.contact

import aguDataSystem.server.domain.contact.Contact
import org.jdbi.v3.core.Handle

class JDBIContactRepository(private val handle: Handle): ContactRepository {
    override fun addContact(cui: String, contact: Contact) {
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
    }

}