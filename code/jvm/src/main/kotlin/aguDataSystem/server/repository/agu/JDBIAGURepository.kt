package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.agu.AGUCreationInfo
import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToDNO
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToLocation
import java.sql.ResultSet
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
	 * TODO do better
	 * @return List of AGUs basic info
	 */
	override fun getAGUsBasicInfo(): List<AGUBasicInfo> {
		logger.info("Getting all AGUs from the database")

		val sql = """
            SELECT 
                agu.cui, agu.eic, agu.name, agu.latitude, agu.longitude, agu.location_name,
                dno.id as dno_id, dno.name as dno_name, dno.region,
                transport_company.id as tc_id, transport_company.name as tc_name
            FROM agu
            LEFT JOIN dno ON agu.dno_id = dno.id
            LEFT JOIN agu_transport_company atc ON agu.cui = atc.agu_cui
            LEFT JOIN transport_company ON atc.company_id = transport_company.id
            ORDER BY agu.cui, transport_company.id
        """.trimIndent()

		val agubasicInfoMap = mutableMapOf<String, AGUBasicInfo>()

		handle.inTransaction<Any, Exception> { conn ->
			val stmt = conn.connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY
			)

			val rs = stmt.executeQuery(sql)

			while (rs.next()) {
				val cui = rs.getString("cui")
				val name = rs.getString("name")
				val eic = rs.getString("eic")
				val dno = mapToDNO(rs)
				val location = mapToLocation(rs)

				val tcId = rs.getInt("tc_id")
				val tcName = rs.getString("tc_name")
				val transportCompany = if (tcName != null) TransportCompany(id = tcId, name = tcName) else null

				if (agubasicInfoMap.containsKey(cui)) {
					transportCompany?.let {
						agubasicInfoMap[cui]?.transportCompanies?.add(it)
					}
				} else {
					val transportCompanies = mutableListOf<TransportCompany>()
					transportCompany?.let {
						transportCompanies.add(it)
					}
					val aguBasicInfo = AGUBasicInfo(
						cui = cui,
						eic = eic,
						name = name,
						dno = dno,
						location = location,
						transportCompanies = transportCompanies
					)
					agubasicInfoMap[cui] = aguBasicInfo
				}
			}

			rs.close()
			stmt.close()

			return@inTransaction agubasicInfoMap.values.toList()
		}
		return agubasicInfoMap.values.toList()
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

		logger.info("AGU with CUI: {}, active state updated to: {} in the database", cui, isActive)
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
