package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.AGU
import org.jdbi.v3.core.Handle

class JDBIAGURepository(private val handle: Handle) : AGURepository {
    /**
     * Get all AGUs
     *
     * @return List of AGUs
     */
    override fun getAGUs(): List<AGU> {
        TODO("Not yet implemented")
    }

    /**
     * Get AGU by CUI
     *
     * @param cui CUI of AGU
     * @return AGU
     */
    override fun getAGUByCUI(cui: Int): AGU {
        TODO("Not yet implemented")
    }

    /**
     * Get AGU by location
     *
     * @param name name of the AGU
     * @return AGU's CUI code
     */
    override fun getCUIByName(name: String): String =
        handle.createQuery("SELECT cui FROM agu WHERE name = :name")
            .bind("name", name)
            .mapTo(String::class.java)
            .findOne()
            .orElseThrow { IllegalStateException("AGU not found for name: $name") }


    /**
     * Add AGU
     *
     * @param agu AGU to add
     * @return AGU
     */
    override fun addAGU(agu: AGU): AGU {
        TODO("Not yet implemented")
    }

    /**
     * Update AGU
     *
     * @param agu AGU to update
     * @return AGU
     */
    override fun updateAGU(agu: AGU): AGU {
        TODO("Not yet implemented")
    }
}