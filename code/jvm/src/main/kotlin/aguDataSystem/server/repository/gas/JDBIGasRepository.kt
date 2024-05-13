package aguDataSystem.server.repository.gas

import aguDataSystem.server.domain.measure.GasMeasure
import java.time.LocalDate
import java.time.LocalTime
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [GasRepository]
 * @see GasRepository
 * @see Handle
 */
class JDBIGasRepository(private val handle: Handle) : GasRepository {

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
            SELECT measure.timestamp, measure.prediction_for, measure.data 
            FROM measure
            WHERE measure.provider_id = :providerId AND 
            measure.timestamp::time >= :timestamp AND 
            measure.prediction_for = measure.timestamp AND
            measure.timestamp::date >= now()::date - :days
            ORDER BY measure.timestamp DESC
            """.trimIndent()
		)
			.bind("providerId", providerId)
			.bind("timestamp", time)
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
            WHERE measure.provider_id = :providerId AND 
            measure.prediction_for >= :day AND 
            measure.prediction_for < :nextDay
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
	 * Gets the gas prediction measures of a provider for a set number of days at a specific time
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
            SELECT measure.timestamp, measure.prediction_for, measure.data 
            FROM measure
            WHERE measure.provider_id = :providerId AND 
            extract (hours from (
                Select inner_measure.prediction_for from measure as inner_measure
                WHERE inner_measure.provider_id = :providerId 
                Order by abs(extract(epoch from inner_measure.prediction_for) - extract(epoch from :timestamp)) 
                limit 1
            )) = extract(hours from measure.prediction_for)
            AND measure.prediction_for <> now()::date
            ORDER BY measure.prediction_for DESC
            LIMIT :days
            """.trimIndent()
		)
			.bind("providerId", providerId)
			.bind("timestamp", LocalDate.now().atTime(time))
			.bind("days", days)
			.mapTo<GasMeasure>()
			.list()

		logger.info(
			"Fetched: {} gas prediction measures for provider with id: {} for the last {} days",
			measures.size,
			providerId,
			days
		)

		return measures
	}

	/**
	 * Adds gas measures to a provider
	 *
	 * @param providerId the id of the provider
	 * @param gasMeasures the gas measures to add
	 */
	override fun addGasMeasuresToProvider(aguCui: String, providerId: Int, gasMeasures: List<GasMeasure>) {
		logger.info("Adding gas measures to provider with id {}", providerId)

		gasMeasures.forEachIndexed { index, it ->
			handle.createUpdate(
				"""
                INSERT INTO measure (agu_cui, provider_id, tag, timestamp, prediction_for, data)
                VALUES (:agu_cui, :providerId, :tag, :timestamp, :predictionFor, :data)
                """.trimIndent()
			)
				.bind("agu_cui", aguCui)
				.bind("providerId", providerId)
				.bind("tag", it::level.name)
				.bind("timestamp", it.timestamp)
				.bind("predictionFor", it.predictionFor)
				.bind("data", it.level)
				.execute()

			logger.info("Added gas measure {} to provider with id {}", index + 1, providerId)
		}

		logger.info("Added {} gas measures to provider with id {}", gasMeasures.size, providerId)
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIGasRepository::class.java)
	}
}