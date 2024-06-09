package aguDataSystem.server.repository.transportCompany

import aguDataSystem.server.domain.company.TransportCompany
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [TransportCompanyRepository]
 * @see TransportCompanyRepository
 * @see Handle
 */
class JDBITransportCompanyRepository(private val handle: Handle) : TransportCompanyRepository {

    /**
     * Get all transport companies
     *
     * @return List of Transport Companies
     */
    override fun getTransportCompanies(): List<TransportCompany> {
        logger.info("Getting all transport companies from the database")

        val companies = handle.createQuery(
            """
            SELECT *
            FROM transport_company
            """.trimIndent()
        )
            .mapTo<TransportCompany>()
            .list()

        logger.info("Retrieved {} transport companies from the database", companies.size)

        return companies
    }

    /**
     * Get transport companies by AGU CUI
     *
     * @param cui CUI of AGU
     * @return List of Transport Companies
     */
    override fun getTransportCompaniesByAGU(cui: String): List<TransportCompany> {
        logger.info("Getting transport companies for AGU with CUI: {} from the database", cui)

        val companies = handle.createQuery(
            """
            SELECT *
            FROM transport_company tc
            JOIN agu_transport_company atc ON tc.name = atc.company_name
            WHERE atc.agu_cui = :cui
            """.trimIndent()
        )
            .bind("cui", cui)
            .mapTo<TransportCompany>()
            .list()

        logger.info("Retrieved {} transport companies for AGU with CUI: {} from the database", companies.size, cui)

        return companies
    }

    /**
     * Get a transport company by ID
     *
     * @param id ID of a transport company
     * @return Transport Company
     */
    override fun getTransportCompanyById(id: Int): TransportCompany? {
        logger.info("Getting transport company by ID: {} from the database", id)

        val company = handle.createQuery(
            """
            SELECT *
            FROM transport_company
            WHERE id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .mapTo<TransportCompany>()
            .singleOrNull()

        if (company == null) {
            logger.info("Transport company not found for ID: {}", id)
        } else {
            logger.info("Retrieved transport company by ID from the database")
        }

        return company
    }

    /**
     * Get a transport company by name
     *
     * @param name Name of a transport company
     * @return Transport Company
     */
    override fun getTransportCompanyByName(name: String): TransportCompany? {
        logger.info("Getting transport company by name: {} from the database", name)

        val company = handle.createQuery(
            """
            SELECT *
            FROM transport_company
            WHERE name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo<TransportCompany>()
            .singleOrNull()

        if (company == null) {
            logger.info("Transport company not found for name: {}", name)
        } else {
            logger.info("Retrieved transport company by name from the database")
        }

        return company
    }

    /**
     * Add a transport company
     *
     * @param name Name of a transport company
     * @return ID of the added transport company
     */
    override fun addTransportCompany(name: String): Int {
        logger.info("Adding transport company to the database")

        val addedCompanyId = handle.createUpdate(
            """
            INSERT INTO transport_company (name)
            VALUES (:name)
            RETURNING id
            """.trimIndent()
        )
            .bind("name", name)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

        logger.info("Transport company with ID: {} added to the database", addedCompanyId)
        return addedCompanyId
    }

    /**
     * Delete a transport company by ID
     *
     * @param id ID of a transport company
     */
    override fun deleteTransportCompany(id: Int) {
        logger.info("Deleting transport company with ID: {} from the database", id)

        handle.createUpdate(
            """
            DELETE FROM transport_company
            WHERE id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .execute()

        logger.info("Transport company with ID: {} deleted from the database", id)
    }

    /**
     * Check if ID stores a transport company
     *
     * @param id ID of a transport company
     * @return True if a transport company is stored, false otherwise
     */
    override fun isTransportCompanyStoredById(id: Int): Boolean {
        logger.info("Checking if transport company with ID: {} exists in the database", id)

        val isStored = handle.createQuery(
            """
            SELECT count(id)
            FROM transport_company
            WHERE id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .mapTo<Int>()
            .one() == 1

        logger.info("Transport company with ID: {} exists in the database: {}", id, isStored)
        return isStored
    }

    /**
     * Check if name stores a transport company
     *
     * @param name Name of a transport company
     * @return True if a transport company is stored, false otherwise
     */
    override fun isTransportCompanyStoredByName(name: String): Boolean {
        logger.info("Checking if transport company with name: {} exists in the database", name)

        val isStored = handle.createQuery(
            """
            SELECT count(name)
            FROM transport_company
            WHERE name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo<Int>()
            .one() == 1

        logger.info("Transport company with name: {} exists in the database: {}", name, isStored)
        return isStored
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JDBITransportCompanyRepository::class.java)
    }
}
