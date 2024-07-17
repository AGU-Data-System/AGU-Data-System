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
	 * Gets the closest gas measures of a provider to [time] for a set number of days.
	 *
	 * @param providerId the id of the provider
	 * @param days the number of days to get the measures from
	 * @param time the time to get the measures for
	 * @return a list of gas measures
	 */
	override fun getGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure> {
		if (days <= 0) {
			logger.error("The number of days must be greater than 0")
			return emptyList()
		}

		logger.info("Getting gas measures for provider with id {} for the last {} days", providerId, days)

		val measures = handle.createQuery(
			"""
        SELECT DISTINCT ON (date_trunc('day', measure.timestamp)) 
            measure.timestamp, measure.prediction_for, measure.data, measure.tank_number 
        FROM measure
        WHERE measure.provider_id = :providerId AND 
        measure.timestamp::date >= now()::date - :days + 1 AND
        measure.prediction_for = measure.timestamp
        ORDER BY date_trunc('day', measure.timestamp), 
                 abs(extract(epoch from measure.timestamp::time) - extract(epoch from :timestamp::time))
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
	 * Gets the gas measures of a provider for a specific day with one measure per hour closest to the top of the hour.
	 *
	 * @param providerId the id of the provider
	 * @param day the day to get the measures from
	 * @return a list of gas measures
	 */
	override fun getGasMeasures(providerId: Int, day: LocalDate): List<GasMeasure> {
		if (day.isAfter(LocalDate.now())) {
			logger.error("The day must be before today")
			return emptyList()
		}

		logger.info("Getting gas measures for provider with id {} for the day {}", providerId, day)

		val measures = handle.createQuery(
			"""
        WITH hourly_measures AS (
            SELECT 
                measure.timestamp, 
                measure.prediction_for, 
                measure.data, 
                measure.tank_number,
                date_trunc('hour', measure.timestamp) AS measure_hour,
                abs(extract(epoch from measure.timestamp) - extract(epoch from date_trunc('hour', measure.timestamp))) AS time_diff
            FROM measure
            WHERE 
                measure.provider_id = :providerId AND 
                measure.prediction_for = measure.timestamp AND
				measure.timestamp::date = :day
        ),
        ranked_measures AS (
            SELECT 
                hourly_measures.*, 
                row_number() OVER (PARTITION BY measure_hour ORDER BY time_diff) AS rank
            FROM hourly_measures
        )
        SELECT 
            timestamp, 
            prediction_for, 
            data, 
            tank_number
        FROM ranked_measures
        WHERE rank = 1
        ORDER BY timestamp
        """.trimIndent()
		)
			.bind("providerId", providerId)
			.bind("day", day)
			.mapTo<GasMeasure>()
			.list()

		logger.info("Fetched gas measures for provider with id: {} for the day {}", providerId, day)

		return measures
	}

	/**
	 * Gets the gas prediction measures of a provider for a set number of days at the closest to a specific time
	 *
	 * @param providerId the id of the provider
	 * @param days the number of days to get the measures from
	 * @param time the time to get the measures for
	 * @return a list of gas measures
	 */
	override fun getPredictionGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure> {
		if (days <= 0) {
			logger.error("The number of days must be positive")
			return emptyList()
		}

		logger.info("Getting gas prediction measures for provider with id {} for the next {} days", providerId, days)

		val measures = handle.createQuery(
			"""
        SELECT DISTINCT ON (date_trunc('day', measure.prediction_for)) 
            measure.timestamp, measure.prediction_for, measure.data, measure.tank_number 
        FROM measure
        WHERE measure.provider_id = :providerId AND 
        measure.prediction_for::date <= now()::date + :days AND
		measure.prediction_for::date >= now()::date AND
        measure.prediction_for <> measure.timestamp
        ORDER BY date_trunc('day', measure.prediction_for), 
                 abs(extract(epoch from measure.prediction_for::time) - extract(epoch from :timestamp::time))
        """.trimIndent()
		)
			.bind("providerId", providerId)
			.bind("timestamp", time)
			.bind("days", days)
			.mapTo<GasMeasure>()
			.list()

		logger.info(
			"Fetched: {} gas prediction measures for provider with id: {} for the next: {} days",
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
	override fun addGasMeasuresToProvider(providerId: Int, gasMeasures: List<GasMeasure>) {
		logger.info("Adding gas measures to provider with id {}", providerId)

		gasMeasures.forEachIndexed { index, measure ->
			handle.createUpdate(
				"""
                INSERT INTO measure (agu_cui, provider_id, tag, timestamp, prediction_for, data, tank_number)
                VALUES ((SELECT provider.agu_cui FROM provider WHERE provider.id = :providerId), 
						:providerId, :tag, :timestamp, :predictionFor, :data, :tankNumber)
                """.trimIndent()
			)
				.bind("providerId", providerId)
				.bind("tag", measure::level.name)
				.bind("timestamp", measure.timestamp)
				.bind("predictionFor", measure.predictionFor)
				.bind("data", measure.level)
				.bind("tankNumber", measure.tankNumber)
				.execute()

			logger.info("Added gas measure {} to provider with id {}", index + 1, providerId)
		}

		logger.info("Added {} gas measures to provider with id {}", gasMeasures.size, providerId)
	}

	/**
	 * Gets the latest gas measures of an AGU
	 *
	 * @param aguCui the CUI of the AGU
	 * @param providerId the id of the provider
	 * @return a list of the latest gas measures, one for each tank
	 */
	override fun getLatestLevels(aguCui: String, providerId: Int): List<GasMeasure> {
		logger.info("Getting the latest gas measures for AGU with CUI {} and provider with id {}", aguCui, providerId)

		val measures = handle.createQuery(
			"""
		SELECT DISTINCT ON (tank_number)
			timestamp,
			prediction_for,
			data,
			tank_number 
		FROM measure
		WHERE agu_cui = :aguCui AND 
		provider_id = :providerId
		ORDER BY tank_number, timestamp DESC
		""".trimIndent()
		)
			.bind("aguCui", aguCui)
			.bind("providerId", providerId)
			.mapTo<GasMeasure>()
			.list()

		logger.info("Fetched the latest gas measures for AGU with CUI {} and provider with id {}", aguCui, providerId)

		return measures
	}

	/**
	 * Deletes all gas measures of an AGU tank
	 *
	 * @param cui the CUI of the AGU
	 * @param number the number of the tank
	 * @return a list of gas measures
	 */
	override fun deleteGasMeasuresByTank(cui: String, number: Int) {
		logger.info("Deleting gas measures for AGU with CUI {} and tank number {}", cui, number)

		val measures = handle.createUpdate(
			"""
		DELETE FROM measure
		WHERE agu_cui = :cui AND tank_number = :number AND tag = 'level'
		""".trimIndent()
		)
			.bind("cui", cui)
			.bind("number", number)
			.execute()

		logger.info("Deleted {} gas measures for AGU with CUI {} and tank number {}", measures, cui, number)
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIGasRepository::class.java)
	}
}