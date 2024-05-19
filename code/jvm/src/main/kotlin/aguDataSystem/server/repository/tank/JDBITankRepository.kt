package aguDataSystem.server.repository.tank

import aguDataSystem.server.domain.tank.Tank
import aguDataSystem.server.domain.tank.TankUpdateInfo
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [TankRepository]
 * @see TankRepository
 * @see Handle
 */
class JDBITankRepository(private val handle: Handle) : TankRepository {

	/**
	 * Adds a tank to an AGU
	 *
	 * @param cui CUI of the AGU
	 * @param tank Tank to add
	 */
	override fun addTank(cui: String, tank: Tank): Int {

		logger.info("Adding tank with number {}, to AGU with CUI {}", tank.number, cui)

		val tankNumber = handle.createUpdate(
			"""
                INSERT INTO tank (agu_cui, number, min_level, max_level, critical_level, load_volume, capacity, correction_factor)
                VALUES (:agu_cui, :number, :min_level, :max_level, :critical_level, :load_volume, :capacity, :correction_factor)
            """.trimIndent()
		)
			.bind("agu_cui", cui)
			.bind("number", tank.number)
			.bind("min_level", tank.levels.min)
			.bind("max_level", tank.levels.max)
			.bind("critical_level", tank.levels.critical)
			.bind("load_volume", tank.loadVolume)
			.bind("capacity", tank.capacity)
			.bind("correction_factor", tank.correctionFactor)
			.executeAndReturnGeneratedKeys(Tank::number.name)
			.mapTo<Int>()
			.one()

		if (tank.number == tankNumber)
			logger.info("Added tank with number {}, to AGU with CUI {}", tankNumber, cui)
		else
			logger.info("Added tank has number {} instead of  {} ,and AGU with CUI {}", tankNumber, tank.number, cui)

		return tankNumber
	}

	/**
	 * Gets all the tanks of an AGU
	 *
	 * @param cui the cui of the AGU
	 * @return a list of all the tanks of the AGU
	 */
	override fun getAGUTanks(cui: String): List<Tank> {
		logger.info("Getting all tanks from the AGU with CUI {}", cui)

		val tanks = handle.createQuery(
			"""
                SELECT * FROM tank
                WHERE agu_cui = :cui
            """.trimIndent()
		)
			.bind("cui", cui)
			.mapTo<Tank>()
			.list()

		logger.info("Retrieved {} tanks from the AGU with CUI {}", tanks.size, cui)

		return tanks
	}

	/**
	 * Gets the tank by its number
	 *
	 * @param cui the cui of the AGU
	 * @param number the number of the tank
	 */
	override fun getTankByNumber(cui: String, number: Int): Tank? {
		logger.info("Getting tank with number {} from the AGU with CUI {}", number, cui)

		val tank = handle.createQuery(
			"""
                SELECT * FROM tank
                WHERE agu_cui = :cui AND number = :number
            """.trimIndent()
		)
			.bind("cui", cui)
			.bind("number", number)
			.mapTo<Tank>()
			.singleOrNull()

		if (tank == null) {
			logger.info("Tank with number {} not found in the AGU with CUI {}", number, cui)
		} else {
			logger.info("Retrieved tank with number {} from the AGU with CUI {}", number, cui)
		}

		return tank
	}

	/**
	 * Deletes a tank from an AGU
	 *
	 * @param cui the cui of the AGU
	 * @param number the number of the tank
	 */
	override fun deleteTank(cui: String, number: Int) {
		logger.info("Deleting tank with number {} from the AGU with CUI {}", number, cui)

		val deleted = handle.createUpdate(
			"""
                DELETE FROM tank
                WHERE agu_cui = :cui AND number = :number
            """.trimIndent()
		)
			.bind("cui", cui)
			.bind("number", number)
			.execute()

		if (deleted == 0) {
			logger.info("Tank with number {} not found in the AGU with CUI {}", number, cui)
		} else {
			logger.info("Deleted tank with number {} from the AGU with CUI {}", number, cui)
		}
	}

	/**
	 * Updates a tank from an AGU
	 *
	 * @param cui the cui of the AGU
	 * @param number the number of the tank
	 * @param tankUpdateInfo the info to update the tank
	 */
	override fun updateTank(cui: String, number: Int, tankUpdateInfo: TankUpdateInfo) {
		logger.info("Updating tank with number {} from the AGU with CUI {}", number, cui)

		val updated = handle.createUpdate(
			"""
                UPDATE tank
                SET min_level = :min_level, 
                max_level = :max_level, 
                critical_level = :critical_level,
                load_volume = :load_volume, 
                capacity = :capacity,
                correction_factor = :correction_factor
                WHERE agu_cui = :cui AND number = :number
            """.trimIndent()
		)
			.bind("min_level", tankUpdateInfo.levels.min)
			.bind("max_level", tankUpdateInfo.levels.max)
			.bind("critical_level", tankUpdateInfo.levels.critical)
			.bind("load_volume", tankUpdateInfo.loadVolume)
			.bind("capacity", tankUpdateInfo.capacity)
			.bind("correction_factor", tankUpdateInfo.correctionFactor)
			.bind("cui", cui)
			.bind("number", number)
			.execute()

		if (updated == 0) {
			logger.info("Tank with number {} not found in the AGU with CUI {}", number, cui)
		} else {
			logger.info("Updated tank with number {} from the AGU with CUI {}", number, cui)
		}
	}

	/**
	 * Checks the number of tanks in an AGU
	 *
	 * @param cui the cui of the AGU
	 * @return the number of tanks in the AGU
	 */
	override fun getNumberOfTanks(cui: String): Int {
		logger.info("Getting the number of tanks in the AGU with CUI {}", cui)

		val count = handle.createQuery(
			"""
                SELECT COUNT(*) FROM tank
                WHERE agu_cui = :cui
            """.trimIndent()
		)
			.bind("cui", cui)
			.mapTo<Int>()
			.one()

		logger.info("Retrieved the number of tanks in the AGU with CUI {}", cui)

		return count
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBITankRepository::class.java)
	}
}
