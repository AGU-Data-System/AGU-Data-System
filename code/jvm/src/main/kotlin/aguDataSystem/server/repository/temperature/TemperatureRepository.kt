package aguDataSystem.server.repository.temperature

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Temperature
import java.time.LocalDateTime

/**
 * Repository for temperature data
 */
interface TemperatureRepository {

	/**
	 * Add a temperature to the repository
	 *
	 * @param temperature the temperature to add
	 */
	fun addTemperature(temperature: Temperature)

	/**
	 * Gets the temperatures for a location
	 *
	 * @param location the location to get the temperature for
	 * @return the temperature for the location
	 * TODO pagination?
	 */
	fun getTemperatureForLocation(location: Location): List<Temperature>

	/**
	 * Gets the temperatures for a location and a specific day
	 *
	 * @param localDateTime the day to get the temperature for
	 * @param location the location to get the temperature for
	 * @return the temperature for the location and given day
	 */
	fun getTemperatureForLocationAndDay(localDateTime: LocalDateTime, location: Location): Temperature
}