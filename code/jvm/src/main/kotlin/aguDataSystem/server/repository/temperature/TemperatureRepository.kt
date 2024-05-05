package aguDataSystem.server.repository.temperature

import aguDataSystem.server.domain.measure.TemperatureMeasure

/**
 * A repository for the temperature measures
 */
interface TemperatureRepository {

    /**
     * Gets the temperature measures of a provider for a set number of days
     *
     * @param providerId the id of the provider
     * @param days the number of days to get the measures from
     * @return a list of temperature measures
     */
    fun getTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure>
}