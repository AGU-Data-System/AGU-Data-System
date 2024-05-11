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
            SELECT m1.provider_id, m1.agu_cui, m1.timestamp, m1.prediction_for, m1.data as min, m2.data as max 
            FROM measure m1 join measure m2
            ON m1.provider_id = m2.provider_id 
            AND m1.prediction_for = m2.prediction_for 
            AND m1.timestamp = m2.timestamp 
            AND m1.agu_cui = m2.agu_cui
            WHERE m1.tag = 'min' 
            AND m2.tag = 'max' 
            AND m1.provider_id = :providerId
            AND m1.prediction_for >= CURRENT_DATE - :days
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