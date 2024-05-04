package aguDataSystem.server.repository.temperature

import aguDataSystem.server.domain.measure.TemperatureMeasure
import org.jdbi.v3.core.Handle

/**
 * JDBI implementation of [TemperatureRepository]
 * @see TemperatureRepository
 * @see Handle
 */
class JDBITemperatureRepository(private val handle: Handle): TemperatureRepository {

    /**
     * Gets the temperature measures of a provider for a set amount of days
     *
     * @param providerId the id of the provider
     * @param days the amount of days to get the measures from
     * @return a list of temperature measures
     */
    override fun getTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure> {
        TODO("Not yet implemented")
    }
}