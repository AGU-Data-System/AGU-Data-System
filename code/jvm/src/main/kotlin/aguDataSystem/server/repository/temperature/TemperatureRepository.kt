package aguDataSystem.server.repository.temperature

import aguDataSystem.server.domain.measure.TemperatureMeasure
import java.time.LocalDate

/**
 * A repository for the temperature measures
 */
interface TemperatureRepository {

	/**
	 * Gets the latest temperature measures of a provider for a set number of days
	 *
	 * @param providerId the id of the provider
	 * @param days the number of days to get the measures from
	 * @return a list of temperature measures
	 */
	fun getTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure>

	/**
	 * Gets the temperature measures of a provider for a specific day
	 *
	 * @param providerId the id of the provider
	 * @param day the day to get the measures from
	 * @return a list of temperature measures
	 */
	fun getTemperatureMeasures(providerId: Int, day: LocalDate): List<TemperatureMeasure>

	/**
	 * Gets the temperature measures of a provider for a set number of days
	 *
	 * @param providerId the id of the provider
	 * @param days the number of days to get the measures from
	 * @return a list of temperature measures
	 */
	fun getPredictionTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure>

	/**
	 * Adds temperature measures to a provider
	 *
	 * @param aguCui the cui of the AGU
	 * @param providerId the id of the provider
	 * @param temperatureMeasures the temperature measures to add
	 */
	fun addTemperatureMeasuresToProvider(aguCui: String, providerId: Int, temperatureMeasures: List<TemperatureMeasure>)
}