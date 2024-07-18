package aguDataSystem.server.repository.temperature

import aguDataSystem.server.domain.measure.TemperatureMeasure

/**
 * A repository for the temperature measures
 */
interface TemperatureRepository {

    /**
     * Gets the past temperature measures of a provider for a set number of days
     *
     * @param providerId the id of the provider
     * @param days the number of days to get the measures from
     * @return a list of temperature measures
     */
    fun getPastTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure>

    /**
     * Gets the prediction temperature measures of a provider for a set number of days
     *
     * @param providerId the id of the provider
     * @param days the number of days to get the measures from
     * @return a list of temperature measures
     */
    fun getPredictionTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure>

    /**
     * Adds temperature measures to a provider
     *
     * @param providerId the id of the provider
     * @param temperatureMeasures the temperature measures to add
     */
    fun addTemperatureMeasuresToProvider(providerId: Int, temperatureMeasures: List<TemperatureMeasure>)

    /**
     * Deletes all temperature measures of an AGU tank
     *
     * @param cui the CUI of the AGU
     * @param number the number of the tank
     */
    fun deleteTemperatureMeasuresByTank(cui: String, number: Int)
}