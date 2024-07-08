package aguDataSystem.server.repository.gas

import aguDataSystem.server.domain.measure.GasMeasure
import java.time.LocalDate
import java.time.LocalTime

/**
 * A repository for the gas measures
 */
interface GasRepository {

	/**
	 * Gets the latest gas measures of a provider for a set number of days
	 *
	 * @param providerId the id of the provider
	 * @param days the number of days to get the measures from
	 * @param time the time to get the measures for
	 * @return a list of gas measures
	 */
	fun getGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure>

	/**
	 * Gets the gas measures of a provider for a specific day
	 *
	 * @param providerId the id of the provider
	 * @param day the day to get the measures from
	 * @return a list of gas measures
	 */
	fun getGasMeasures(providerId: Int, day: LocalDate): List<GasMeasure>

	/**
	 * Gets the gas prediction measures of a provider for a set number of days at the closest to a specific time
	 *
	 * @param providerId the id of the provider
	 * @param days the number of days to get the measures from
	 * @param time the time to get the measures for
	 * @return a list of gas measures
	 */
	fun getPredictionGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure>

	/**
	 * Adds gas measures to a provider
	 *
	 * @param providerId the id of the provider
	 * @param gasMeasures the gas measures to add
	 */
	fun addGasMeasuresToProvider(providerId: Int, gasMeasures: List<GasMeasure>)

	/**
	 * Gets the latest gas measures of an AGU
	 *
	 * @param aguCui the CUI of the AGU
	 * @return a list of the latest gas measures, one for each tank
	 */
	fun getLatestLevels(aguCui: String): List<GasMeasure>
}