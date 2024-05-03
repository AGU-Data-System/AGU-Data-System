package aguDataSystem.server.repository.dno

import aguDataSystem.server.domain.company.DNO
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [DNORepository]
 * @see DNORepository
 * @see Handle
 */
class JDBIDNORepository(private val handle: Handle) : DNORepository {

    /**
     * Adds a DNO to the repository
     *
     * @param name the name of the DNO
     */
    override fun addDNO(name: String) {

        logger.info("Adding DNO with name {}", name)

        val id = handle.createUpdate(
            """
                INSERT INTO dno (name)
                VALUES (:name)
            """.trimIndent()
        )
            .bind("name", name)
            .executeAndReturnGeneratedKeys(DNO::id.name)

        logger.info("Added DNO with name {} and it's id is {}", name, id)
    }

    /**
     * Gets DNO by its name
     *
     * @param name the name of the DNO
     * @return the DNO with the given name or null if it doesn't exist
     */
    override fun getByName(name: String): DNO? {

        logger.info("Getting DNO with name {}", name)

        val dno = handle.createQuery(
            """
                SELECT *
                FROM dno
                WHERE name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo<DNO>()
            .singleOrNull()

        return if (dno == null) {
            logger.info("DNO not found for name: $name")
            null
        } else {
            logger.info("Retrieved DNO by name from the database")
            dno
        }
    }

    /**
     * Gets DNO by its id
     *
     * @param id the id of the DNO
     * @return the DNO with the given id or null if it doesn't exist
     */
    override fun getById(id: Int): DNO? {

        logger.info("Getting DNO with id {}", id)

        val dno = handle.createQuery(
            """
                SELECT *
                FROM dno
                WHERE id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .mapTo<DNO>()
            .singleOrNull()

        if (dno == null) {
            logger.info("DNO not found for id: $id")
        } else {
            logger.info("Retrieved DNO by id from the database")
        }

        return dno
    }

    /**
     * Checks if a DNO with the given name is stored
     *
     * @param name the name of the DNO
     * @return true if the DNO is stored, false otherwise
     */
    override fun isDNOStored(name: String): Boolean {

        logger.info("Checking if DNO with name {} is stored", name)

        val find = handle.createQuery(
            """
                SELECT COUNT(*)
                FROM dno
                WHERE name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo<Int>()
            .singleOrNull() == 1

        if (find) {
            logger.info("DNO with name {} is stored", name)
        } else {
            logger.info("DNO with name {} is not stored", name)
        }

        return find
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JDBIDNORepository::class.java)
    }
}
