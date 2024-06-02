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
            SELECT m1.provider_id, m1.agu_cui, m1.timestamp, m1.prediction_for, m1.data as min, 
			m2.data as max 
            FROM measure m1 join measure m2
            ON m1.provider_id = m2.provider_id AND m1.prediction_for = m2.prediction_for 
            AND m1.timestamp = m2.timestamp AND m1.agu_cui = m2.agu_cui
            WHERE m1.tag = :minTag AND m2.tag = :maxTag AND m1.provider_id = :providerId
            ORDER BY m1.timestamp DESC, m1.prediction_for
            LIMIT :days
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
            ORDER BY prediction_for DESC
            """
		)
			.bind("providerId", providerId)
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
            INSERT INTO measure (agu_cui, provider_id, tag, timestamp, prediction_for, data)
            VALUES ((SELECT provider.agu_cui FROM provider WHERE provider.id = :providerId), 
				:providerId, :tag, :timestamp, :predictionFor, :data)
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
			.execute()
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBITemperatureRepository::class.java)
	}
}