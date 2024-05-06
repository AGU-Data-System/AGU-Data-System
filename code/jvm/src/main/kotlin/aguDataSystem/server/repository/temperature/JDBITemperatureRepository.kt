package aguDataSystem.server.repository.temperature

import aguDataSystem.server.domain.measure.TemperatureMeasure
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [TemperatureRepository]
 * @see TemperatureRepository
 * @see Handle
 */
class JDBITemperatureRepository(private val handle: Handle): TemperatureRepository {

    /**
     * Gets the latest temperature measures of a provider for a set number of days
     *
     * @param providerId the id of the provider
     * @param days the number of days to get the measures from
     * @return a list of temperature measures
     */
    override fun getTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure> {
        logger.info("Getting temperature measures for provider {}, for {} days", providerId, days)
        val tempMeasures = handle.createQuery(
            """
            SELECT * FROM measure
            WHERE provider_id = :providerId
            AND prediction_for >= CURRENT_DATE - :days
            ORDER BY prediction_for DESC
            """
        )
            .bind("providerId", providerId)
            .bind("days", days)
            .mapTo<TemperatureMeasure>()
            .list()

        logger.info("Got {} temperature measures", tempMeasures.size)
        return tempMeasures
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JDBITemperatureRepository::class.java)
    }
}