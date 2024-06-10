package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.agu.AGUCreationInfo
import aguDataSystem.server.domain.gasLevels.GasLevels
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [AGURepository]
 * @see AGURepository
 * @see Handle
 */
class JDBIAGURepository(private val handle: Handle) : AGURepository {

	/**
	 * Get all AGUs
	 *
	 * @return List of AGUs basic info
	 */
	override fun getAGUsBasicInfo(): List<AGUBasicInfo> {
		logger.info("Getting all AGUs from the database")

		val aGUs = handle.createQuery(
			"""
        SELECT 
            agu.cui, agu.name, agu.latitude, agu.longitude, agu.location_name,
            dno.id as dno_id, dno.name as dno_name, dno.region,
            transport_company.id as tc_id, transport_company.name as tc_name
        FROM agu
        LEFT JOIN dno ON agu.dno_id = dno.id
        LEFT JOIN agu_transport_company atc ON agu.cui = atc.agu_cui
        LEFT JOIN transport_company ON atc.company_id = transport_company.id
        ORDER BY agu.cui
        """.trimIndent()
		)
			.mapTo<AGUBasicInfo>()
			.list()

		logger.info("Retrieved {} AGUs from the database", aGUs.size)

		return aGUs
	}

	/**
	 * Get AGU by CUI
	 *
	 * @param cui CUI of AGU
	 * @return AGU
	 */
	override fun getAGUByCUI(cui: String): AGU? {
		logger.info("Getting AGU by CUI: {} from the database", cui)

		val agu = handle.createQuery(
			"""
			SELECT agu.*, dno.id as dno_id, dno.name as dno_name, dno.region
			FROM agu 
			join dno 
			on agu.dno_id = dno.id
            WHERE agu.cui = :agu_cui 
            group by dno.id, agu.cui
            """.trimIndent()
		)
			.bind("agu_cui", cui)
			.mapTo<AGU>()
			.singleOrNull()

		if (agu == null) {
			logger.info("AGU not found for CUI: {}", cui)
		} else {
			logger.info("Retrieved AGU by CUI from the database")
		}

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
			.mapTo<String>()
			.singleOrNull()

		if (cui == null) {
			logger.info("AGU not found for name: {}", name)
		} else {
			logger.info("Retrieved CUI by AGU name from the database")
		}

		return cui
	}


	/**
	 * Add AGU
	 *
	 * @param aguCreationInfo AGU parameters to create the AGU from
	 * @param dnoID DNO ID
	 * @return AGU's CUI code
	 */
	override fun addAGU(aguCreationInfo: AGUCreationInfo, dnoID: Int): String {
		logger.info("Adding AGU to the database")

		val addedAGUCUI = handle.createUpdate(
			"""
            INSERT INTO agu (
            cui, eic, name, is_favorite, min_level, max_level, critical_level, load_volume, correction_factor, latitude, longitude, 
            location_name, dno_id, is_active, notes, training, image
            ) 
            VALUES (
            :cui, :eic, :name, :isFavorite, :minLevel, :maxLevel, :criticalLevel, :loadVolume, :correctionFactor, :latitude, :longitude, 
            :locationName, :dnoId, :isActive, :notes, :training::json, :image
            ) returning cui
            """.trimIndent()
		)
			.bind("cui", aguCreationInfo.cui)
			.bind("eic", aguCreationInfo.eic)
			.bind("name", aguCreationInfo.name)
			.bind("isFavorite", aguCreationInfo.isFavorite)
			.bind("minLevel", aguCreationInfo.levels.min)
			.bind("maxLevel", aguCreationInfo.levels.max)
			.bind("criticalLevel", aguCreationInfo.levels.critical)
			.bind("loadVolume", aguCreationInfo.loadVolume)
			.bind("correctionFactor", aguCreationInfo.correctionFactor)
			.bind("latitude", aguCreationInfo.location.latitude)
			.bind("longitude", aguCreationInfo.location.longitude)
			.bind("locationName", aguCreationInfo.location.name)
			.bind("dnoId", dnoID)
			.bind("isActive", aguCreationInfo.isActive)
			.bind("notes", aguCreationInfo.notes)
			.bind("training", aguCreationInfo.training)
			.bind("image", aguCreationInfo.image)
			.executeAndReturnGeneratedKeys(AGUCreationInfo::cui.name)
			.mapTo<String>()
			.one()

		logger.info("AGU with CUI: {} added to the database", addedAGUCUI)
		return addedAGUCUI
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
            SET name = :name, is_favorite = :isFavorite, min_level = :minLevel, max_level = :maxLevel, 
            critical_level = :criticalLevel, load_volume = :loadVolume, latitude = :latitude, longitude = :longitude, 
            location_name = :locationName, dno_id = :dnoId, notes = :notes, training = :training::json, image = :image
            WHERE cui = :cui
            """.trimIndent()
		)
			.bind("cui", agu.cui)
			.bind("name", agu.name)
			.bind("isFavorite", agu.isFavorite)
			.bind("minLevel", agu.levels.min)
			.bind("maxLevel", agu.levels.max)
			.bind("criticalLevel", agu.levels.critical)
			.bind("loadVolume", agu.loadVolume)
			.bind("latitude", agu.location.latitude)
			.bind("longitude", agu.location.longitude)
			.bind("locationName", agu.location.name)
			.bind("dnoId", agu.dno.id)
			.bind("notes", agu.notes)
			.bind("training", agu.training)
			.bind("image", agu.image)
			.execute()

		logger.info("AGU with CUI: {}, updated in the database", agu.cui)
		return agu
	}

	/**
	 * Update AGU favourite state
	 *
	 * @param cui CUI of AGU
	 * @param isFavorite New favourite state
	 */
	override fun updateFavouriteState(cui: String, isFavorite: Boolean) {
		logger.info("Updating AGU favourite state in the database")

		handle.createUpdate(
			"""
			UPDATE agu 
			SET is_favorite = :isFavorite
			WHERE cui = :cui
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("isFavorite", isFavorite)
			.execute()

		logger.info("AGU with CUI: {}, favourite state updated in the database", cui)
	}

	/**
	 * Update AGU active state
	 *
	 * @param cui CUI of AGU
	 * @param isActive New active state
	 */
	override fun updateActiveState(cui: String, isActive: Boolean) {
		logger.info("Updating AGU active state in the database")

		handle.createUpdate(
			"""
			UPDATE agu 
			SET is_active = :isActive
			WHERE cui = :cui
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("isActive", isActive)
			.execute()

		logger.info("AGU with CUI: {}, active state updated in the database", cui)
	}

	/**
	 * Checks whether an AGU exists
	 *
	 * @param cui CUI of AGU
	 * @return True if AGU exists, false otherwise
	 */
	override fun isAGUStored(cui: String): Boolean {
		logger.info("Checking if AGU with CUI: {} exists in the database", cui)

		val isStored = handle.createQuery(
			"""
            SELECT count(cui) FROM agu 
            WHERE cui = :cui
            """.trimIndent()
		)
			.bind("cui", cui)
			.mapTo<Int>()
			.one() == 1

		logger.info("AGU with CUI: {} exists in the database: {}", cui, isStored)
		return isStored
	}

	/**
	 * Update gas levels of an AGU
	 *
	 * @param cui CUI of AGU
	 * @param levels New gas levels
	 */
	override fun updateGasLevels(cui: String, levels: GasLevels) {
		logger.info("Updating gas levels of AGU with CUI: {} in the database", cui)

		handle.createUpdate(
			"""
			UPDATE agu 
			SET min_level = :minLevel, max_level = :maxLevel, critical_level = :criticalLevel
			WHERE cui = :cui
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("minLevel", levels.min)
			.bind("maxLevel", levels.max)
			.bind("criticalLevel", levels.critical)
			.execute()

		logger.info("Gas levels of AGU with CUI: {} updated in the database", cui)
	}

	/**
	 * Update notes of an AGU
	 *
	 * @param cui CUI of AGU
	 * @param notes New notes
	 */
	override fun updateNotes(cui: String, notes: String?) {
		logger.info("Updating notes of AGU with CUI: {} in the database", cui)

		handle.createUpdate(
			"""
			UPDATE agu 
			SET notes = :notes
			WHERE cui = :cui
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("notes", notes)
			.execute()

		logger.info("Notes of AGU with CUI: {} updated in the database", cui)
	}

	/**
	 * Update the training model of an AGU
	 *
	 * @param cui CUI of AGU
	 * @param model New training model
	 */
	override fun updateTrainingModel(cui: String, model: String?) {
		logger.info("Updating training model of AGU with CUI: {} in the database", cui)

		handle.createUpdate(
			"""
			UPDATE agu 
			SET training = :training::json
			WHERE cui = :cui
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("training", model)
			.execute()

		logger.info("Training model of AGU with CUI: {} updated in the database", cui)
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIAGURepository::class.java)
	}
}
