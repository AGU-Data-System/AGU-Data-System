package aguDataSystem.server.repository.gas

import aguDataSystem.server.domain.measure.GasMeasure
import org.jdbi.v3.core.Handle
import java.time.LocalDate
import java.time.LocalTime
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [GasRepository]
 * @see GasRepository
 * @see Handle
 */
class JDBIGasRepository(private val handle: Handle): GasRepository {

    /**
     * Gets the latest gas measures of a provider for a set number of days
     *
     * @param providerId the id of the provider
     * @param days the number of days to get the measures from
     * @param time the time to get the measures for
     * @return a list of gas measures
     */
    override fun getGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure> {
        logger.info("Getting gas measures for provider with id {} for the last {} days", providerId, days)

        val measures = handle.createQuery(
            """
            SELECT measure.timestamp, measure.prediction_for, measure.data FROM measure
            WHERE measure.provider_id = :providerId AND measure.timestamp >= :timestamp
            ORDER BY measure.timestamp DESC
            LIMIT :days
            """.trimIndent()
        )
            .bind("providerId", providerId)
            .bind("timestamp", LocalDate.now().atTime(time))
            .bind("days", days)
            .mapTo<GasMeasure>()
            .list()

        logger.info("Fetched gas measures for provider with id: {} for the last {} days", providerId, days)

        return measures
    }

    /**
     * Gets the gas measures of a provider for a specific day
     *
     * @param providerId the id of the provider
     * @param day the day to get the measures from
     * @return a list of gas measures
     */
    override fun getGasMeasures(providerId: Int, day: LocalDate): List<GasMeasure> {
        logger.info("Getting gas measures for provider with id {} for the day {}", providerId, day)

        val measures = handle.createQuery(
            """
            SELECT measure.timestamp, measure.prediction_for, measure.data FROM measure
            WHERE measure.provider_id = :providerId AND measure.timestamp >= :day AND measure.timestamp < :nextDay
            """.trimIndent()
        )
            .bind("providerId", providerId)
            .bind("day", day)
            .bind("nextDay", day.plusDays(1))
            .mapTo<GasMeasure>()
            .list()

        logger.info("Fetched gas measures for provider with id: {} for the day {}", providerId, day)

        return measures
    }

    /**
     * Gets the gas prediction measures of a provider for a set number of days
     *
     * @param providerId the id of the provider
     * @param days the number of days to get the measures from
     * @param time the time to get the measures for
     * @return a list of gas measures
     */
    override fun getPredictionGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure> {
        logger.info("Getting gas prediction measures for provider with id {} for the last {} days", providerId, days)

        val measures = handle.createQuery(
            """
            SELECT measure.timestamp, measure.prediction_for, measure.data FROM measure
            WHERE measure.provider_id = :providerId AND measure.prediction_for >= :timestamp
            ORDER BY measure.prediction_for DESC
            LIMIT :days
            """.trimIndent()
        )
            .bind("providerId", providerId)
            .bind("timestamp", LocalDate.now().atTime(time))
            .bind("days", days)
            .mapTo<GasMeasure>()
            .list()

        logger.info("Fetched gas prediction measures for provider with id: {} for the last {} days", providerId, days)

        return measures
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JDBIGasRepository::class.java)
    }
}