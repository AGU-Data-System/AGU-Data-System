package aguDataSystem.server.repository.temperature

import aguDataSystem.server.domain.measure.TemperatureMeasure
import java.time.LocalDate
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [TemperatureRepository]
 * @see TemperatureRepository
 * @see Handle
 */
class JDBITemperatureRepository(private val handle: Handle) : TemperatureRepository {

	/**
	 * Gets the latest temperature measures of a provider for a set number of days
	 *
	 * @param providerId the id of the provider
	 * @param days the number of days to get the measures from
	 * @return a list of temperature measures
	 */
	override fun getTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure> {
		if (days <= 0) {
			logger.error("The number of days must be positive for provider {}", providerId)
			return emptyList()
		}

		logger.info("Getting temperature measures for provider {}, for {} days", providerId, days)
		val tempMeasures = handle.createQuery(
			"""
        WITH latest_measures AS (
            SELECT 
                m.provider_id,
                m.agu_cui,
                m.timestamp,
                m.prediction_for::date as prediction_date,
                m.data,
                m.tag,
                ROW_NUMBER() OVER (PARTITION BY m.provider_id, m.prediction_for::date, m.tag ORDER BY m.timestamp DESC) as rn
            FROM measure m
            WHERE 
                m.provider_id = :providerId AND 
                m.prediction_for::date >= CURRENT_DATE AND 
                m.prediction_for::date < CURRENT_DATE + :days
        )
        SELECT 
            min_measures.provider_id, 
            min_measures.agu_cui, 
            min_measures.timestamp, 
            min_measures.prediction_date as prediction_for, 
            min_measures.data as min, 
            max_measures.data as max
        FROM 
            latest_measures min_measures
        JOIN 
            latest_measures max_measures 
        ON 
            min_measures.provider_id = max_measures.provider_id AND 
            min_measures.prediction_date = max_measures.prediction_date AND 
            min_measures.agu_cui = max_measures.agu_cui
        WHERE 
            min_measures.tag = :minTag AND 
            max_measures.tag = :maxTag AND 
            min_measures.rn = 1 AND 
            max_measures.rn = 1
        ORDER BY 
            min_measures.prediction_date DESC
        """
		)
			.bind("providerId", providerId)
			.bind("minTag", TemperatureMeasure::min.name)
			.bind("maxTag", TemperatureMeasure::max.name)
			.bind("days", days)
			.mapTo<TemperatureMeasure>()
			.list()

		logger.info("Got {} temperature measures", tempMeasures.size)
		return tempMeasures
	}

	/**
	 * Gets the temperature measures of a provider for a specific day
	 *
	 * @param providerId the id of the provider
	 * @param day the day to get the measures from
	 * @return a list of temperature measures
	 */
	override fun getTemperatureMeasures(providerId: Int, day: LocalDate): List<TemperatureMeasure> {
		logger.info("Getting temperature measures for provider {}, for the day {}", providerId, day)
		val tempMeasures = handle.createQuery(
			"""
            SELECT m1.provider_id, m1.agu_cui, m1.timestamp, m1.prediction_for, m1.data as min, 
            m2.data as max 
            FROM measure m1 join measure m2
            ON m1.provider_id = m2.provider_id AND m1.prediction_for = m2.prediction_for 
            AND m1.timestamp = m2.timestamp AND m1.agu_cui = m2.agu_cui
            WHERE m1.tag = :minTag AND m2.tag = :maxTag AND m1.provider_id = :providerId
            AND m1.prediction_for::date = :day
            ORDER BY prediction_for DESC
            """
		)
			.bind("providerId", providerId)
			.bind("minTag", TemperatureMeasure::min.name)
			.bind("maxTag", TemperatureMeasure::max.name)
			.bind("day", day)
			.mapTo<TemperatureMeasure>()
			.list()

		logger.info("Got {} temperature measures for {}", tempMeasures.size, day)
		return tempMeasures
	}

	/**
	 * Gets the temperature measures of a provider for a set number of days
	 *
	 * @param providerId the id of the provider
	 * @param days the number of days to get the measures from
	 * @return a list of temperature measures
	 */
	override fun getPredictionTemperatureMeasures(providerId: Int, days: Int): List<TemperatureMeasure> {
		if (days <= 0) {
			logger.error("The number of days must be positive")
			return emptyList()
		}

		logger.info("Getting prediction temperature measures for provider {}, for {} days", providerId, days)

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
            AND m1.prediction_for >= CURRENT_DATE
            ORDER BY prediction_for
			LIMIT :days
            """
		)
			.bind("providerId", providerId)
			.bind("days", days)
			.mapTo<TemperatureMeasure>()
			.list()

		logger.info("Got {} prediction temperature measures for {} days", tempMeasures.size, days)
		return tempMeasures
	}

	/**
	 * Adds temperature measures to a provider
	 *
	 * @param providerId the id of the provider
	 * @param temperatureMeasures the temperature measures to add
	 */
	override fun addTemperatureMeasuresToProvider(
		providerId: Int,
		temperatureMeasures: List<TemperatureMeasure>
	) {
		logger.info("Adding temperature measures to provider {}", providerId)
		temperatureMeasures.forEachIndexed { index, tempMeasure ->
			// inserting min temp
			addTagTemperature(providerId, tempMeasure, tempMeasure::min.name)

			// inserting max temp
			addTagTemperature(providerId, tempMeasure, tempMeasure::max.name)

			logger.info("Added temperature measure {} to provider {}", index + 1, providerId)
		}
		logger.info("Added {} temperature measures to provider {}", temperatureMeasures.size, providerId)
	}

	/**
	 * Deletes all temperature measures of an AGU tank
	 *
	 * @param cui the CUI of the AGU
	 * @param number the number of the tank
	 */
	override fun deleteTemperatureMeasuresByTank(cui: String, number: Int) {
		logger.info("Deleting temperature measures for AGU with CUI {} and tank number {}", cui, number)
		handle.createUpdate(
			"""
			DELETE FROM measure
			WHERE agu_cui = :cui AND tank_number = :number AND tag IN ('min', 'max')
			"""
		)
			.bind("cui", cui)
			.bind("number", number)
			.execute()
		logger.info("Deleted temperature measures for AGU with CUI {} and tank number {}", cui, number)
	}

	/**
	 * Adds a temperature measure to a provider by its tag
	 *
	 * @param providerId the id of the provider
	 * @param temperatureMeasure the temperature measure to add
	 * @param tag the tag of the temperature measure
	 */
	private fun addTagTemperature(
		providerId: Int,
		temperatureMeasure: TemperatureMeasure,
		tag: String
	) {
		handle.createUpdate(
			"""
            INSERT INTO measure (agu_cui, provider_id, tag, timestamp, prediction_for, data, tank_number)
            VALUES ((SELECT provider.agu_cui FROM provider WHERE provider.id = :providerId), 
				:providerId, :tag, :timestamp, :predictionFor, :data, :tankNumber)
            """
		)
			.bind("providerId", providerId)
			.bind("tag", tag)
			.bind("timestamp", temperatureMeasure.timestamp)
			.bind("predictionFor", temperatureMeasure.predictionFor)
			.bind(
				"data", when (tag) {
					TemperatureMeasure::min.name -> temperatureMeasure.min
					TemperatureMeasure::max.name -> temperatureMeasure.max
					else -> throw IllegalArgumentException("Invalid tag")
				}
			)
			.bind("tankNumber", TEMPERATURE_TANK_NUMBER)
			.execute()
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBITemperatureRepository::class.java)
		private const val TEMPERATURE_TANK_NUMBER = 1
	}
}