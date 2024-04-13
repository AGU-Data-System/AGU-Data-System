package aguDataSystem.server.repository.temperature

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Temperature
import java.time.LocalDateTime
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * A JDBI implementation of [TemperatureRepository]
 * @see TemperatureRepository
 * @see Handle
 */
class JDBITemperatureRepository(private val handle: Handle) : TemperatureRepository {
	/**
	 * Add a temperature to the repository
	 *
	 * @param temperature the temperature to add
	 */
	override fun addTemperature(temperature: Temperature) {

		logger.info("Adding temperature: $temperature")

		handle.createUpdate(
			"""
			INSERT INTO temperature (date, min, max, timestamp,latitude, longitude)
			VALUES (:date, :min, :max, :timestamp, :latitude, :longitude)
		""".trimIndent()
		)
			.bind("date", temperature.date)
			.bind("min", temperature.min)
			.bind("max", temperature.max)
			.bind("timestamp", temperature.fetchTimeStamp)
			.bind("latitude", temperature.location.latitude)
			.bind("longitude", temperature.location.longitude)
			.execute()

		logger.info("Added temperature: $temperature")
	}

	/**
	 * Gets the temperatures for a location
	 *
	 * @param location the location to get the temperature for
	 * @return the temperature for the location
	 * TODO pagination?
	 */
	override fun getTemperatureForLocation(location: Location): List<Temperature> {

		logger.info("Getting temperature for location: $location")

		val temperatures = handle.createQuery(
			"""
			SELECT *
			FROM temperature
			WHERE latitude = :latitude AND longitude = :longitude
		""".trimIndent()
		)
			.bind("latitude", location.latitude)
			.bind("longitude", location.longitude)
			.mapTo<Temperature>()
			.list()

		logger.info("Got ${temperatures.size} temperatures for location: $location")

		return temperatures
	}

	/**
	 * Gets the temperatures for a location and a specific day
	 *
	 * @param localDateTime the day to get the temperature for
	 * @param location the location to get the temperature for
	 * @return the temperature for the location and given day
	 */
	override fun getTemperatureForLocationAndDay(localDateTime: LocalDateTime, location: Location): Temperature {

		logger.info("Getting temperature for location: $location and day: $localDateTime")

		val temperature = handle.createQuery(
			"""
			SELECT *
			FROM temperature
			WHERE latitude = :latitude AND longitude = :longitude AND date = :date
		""".trimIndent()
		)
			.bind("latitude", location.latitude)
			.bind("longitude", location.longitude)
			.bind("date", localDateTime)
			.mapTo<Temperature>()
			.one()

		logger.info("Got temperature: $temperature for location: $location and day: $localDateTime")

		return temperature
	}

	/**
	 * Get all locations with temperature data.
	 *
	 * @return List of all locations with temperature data.
	 */
	override fun getTemperatureLocations(): List<Location> {

		logger.info("Getting all locations with temperature data")

		val locations = handle.createQuery(
			"""
			SELECT DISTINCT latitude, longitude
			FROM temperature
		""".trimIndent()
		)
			.mapTo<Location>()
			.list()

		logger.info("Got ${locations.size} locations with temperature data")

		return locations
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBITemperatureRepository::class.java)
	}
}