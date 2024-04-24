package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.AGU
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

class JDBIAGURepository(private val handle: Handle) : AGURepository {
    /**
     * Get all AGUs
     *
     * @return List of AGUs
     */
    override fun getAGUs(): List<AGU> {
        logger.info("Getting all AGUs from the database")

        val aGUs = handle.createQuery(
            """
            SELECT * FROM agu join contacts 
            on agu.cui = contacts.agu_cui 
            order by agu.cui
            """.trimIndent()
        )
            .mapTo<AGU>()
            .list()

        logger.info("Retrieved ${aGUs.size} AGUs from the database")

        return aGUs
    }

    /**
     * Get AGU by CUI
     *
     * @param cui CUI of AGU
     * @return AGU
     */
    override fun getAGUByCUI(cui: String): AGU? {
        logger.info("Getting AGU by CUI {}, from the database", cui)

        val agu = handle.createQuery(
            """
            SELECT * FROM agu join contacts 
            on agu.cui = contacts.agu_cui 
            WHERE agu.cui = :cui 
            group by agu.cui
            """.trimIndent()
        )
            .bind("cui", cui)
            .mapTo<AGU>()
            .one()

        if (agu == null) {
            logger.info("AGU not found for CUI: $cui")
            return null
        }

        logger.info("Retrieved AGU by CUI from the database")
        return agu
    }

    /**
     * Get AGU by location
     *
     * @param name name of the AGU
     * @return AGU's CUI code
     */
    override fun getCUIByName(name: String): String? {

        logger.info("Getting AGU by name: {}, from the database", name)

        val cui = handle.createQuery(
            """
            SELECT cui FROM agu 
            WHERE name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo(String::class.java)
            .one()

        if (cui == null) {
            logger.info("AGU CUI not found for name: $name")
            return null
        }

        logger.info("Retrieved CUI by AGU name from the database")
        return cui
    }


    /**
     * Add AGU
     *
     * @param agu AGU to add
     * @return AGU
     */
    override fun addAGU(agu: AGU): AGU {
        logger.info("Adding AGU to the database")

        handle.createUpdate(
            """
            INSERT INTO agu (cui, name, is_favorite, min_level, max_level, critical_level, capacity, latitude, longitude, location_name, dno_id, notes, training, image) 
            VALUES (:cui, :name, :isFavorite, :minLevel, :maxLevel, :criticalLevel, :capacity, :latitude, :longitude, :locationName, :dnoId, :notes, :training, :image)
            """.trimIndent()
        )
            .bind("cui", agu.cui)
            .bind("name", agu.name)
            .bind("isFavorite", agu.isFavorite)
            .bind("minLevel", agu.minLevel)
            .bind("maxLevel", agu.maxLevel)
            .bind("criticalLevel", agu.criticalLevel)
            .bind("capacity", agu.capacity)
            .bind("latitude", agu.latitude)
            .bind("longitude", agu.longitude)
            .bind("locationName", agu.locationName)
            .bind("dnoId", agu.dnoId)
            .bind("notes", agu.notes)
            .bind("training", agu.training)
            .bind("image", agu.image)
            .execute()

        logger.info("AGU with CUI: {}, added to the database", agu.cui)
        return agu
    }

    /**
     * Update AGU
     *
     * @param agu AGU to update
     * @return AGU
     */
    override fun updateAGU(agu: AGU): AGU {
        logger.info("Updating AGU in the database")

        handle.createUpdate(
            """
            UPDATE agu 
            SET name = :name, is_favorite = :isFavorite, min_level = :minLevel, max_level = :maxLevel, critical_level = :criticalLevel, capacity = :capacity, latitude = :latitude, longitude = :longitude, location_name = :locationName, dno_id = :dnoId, notes = :notes, training = :training, image = :image
            WHERE cui = :cui
            """.trimIndent()
        )
            .bind("cui", agu.cui)
            .bind("name", agu.name)
            .bind("isFavorite", agu.isFavorite)
            .bind("minLevel", agu.minLevel)
            .bind("maxLevel", agu.maxLevel)
            .bind("criticalLevel", agu.criticalLevel)
            .bind("capacity", agu.capacity)
            .bind("latitude", agu.latitude)
            .bind("longitude", agu.longitude)
            .bind("locationName", agu.locationName)
            .bind("dnoId", agu.dnoId)
            .bind("notes", agu.notes)
            .bind("training", agu.training)
            .bind("image", agu.image)
            .execute()

        logger.info("AGU with CUI: {}, updated in the database", agu.cui)
        return agu
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JDBIAGURepository::class.java)
    }
}
