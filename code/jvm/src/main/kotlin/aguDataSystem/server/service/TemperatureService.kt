package aguDataSystem.server.service

import aguDataSystem.server.repository.TransactionManager
import org.springframework.stereotype.Service

/**
 * Service for temperature data
 */
@Service
class TemperatureService(private val transactionManager : TransactionManager, private val fetchService: FetchService) {

	/**
	 * Gets all the temperature locations
	 *
	 * @return a list of all temperature locations
	 */
	fun getTemperatureLocations() = transactionManager.run { transaction ->
		transaction.temperatureRepository.getTemperatureLocations()
	}

	/**
	 * Schedules a fetch to every temperature location and
	 * saves the data to the database
	 *
	 * TODO clean the logic
	 */
	fun addFetchTemperatureToFetcher() {
		transactionManager.run { transaction ->
			val tempLocations = getTemperatureLocations()
			val tempProvider = DataProviders.Temperature
			repeat(tempLocations.size) {
				val fetchURl = tempProvider.createTemperatureURL(tempLocations[it].toString())
				val data = transaction.fetcherRepository(fetchURl).cleanData() // gives a list of temperatures based on the day
				data.forEach { temp -> transaction.temperatureRepository.addTemperature(temperature = temp) }
			}
		}
	}
}