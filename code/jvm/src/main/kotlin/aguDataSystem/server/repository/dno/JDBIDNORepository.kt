package aguDataSystem.server.repository.dno

import aguDataSystem.server.domain.company.DNO
import org.jdbi.v3.core.Handle

class JDBIDNORepository(private val handle: Handle) : DNORepository {
    override fun addDNO(name: String) {
        handle.createUpdate(
            """
                INSERT INTO dno (name)
                VALUES (:name)
            """.trimIndent()
        )
            .bind("name", name)
            .execute()
    }

    override fun getByName(name: String): DNO? {
        return handle.createQuery(
            """
                SELECT *
                FROM dno
                WHERE name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo(DNO::class.java)
            .findOne()
            .orElse(null)
    }

    override fun getById(id: Int): DNO? {
        return handle.createQuery(
            """
                SELECT *
                FROM dno
                WHERE id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .mapTo(DNO::class.java)
            .findOne()
            .orElse(null)
    }

    override fun isDNOStored(name: String): Boolean {
        return handle.createQuery(
            """
                SELECT COUNT(*)
                FROM dno
                WHERE name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo(Int::class.java)
            .one() > 0
    }
}