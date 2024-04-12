package aguDataSystem.server.repository

import aguDataSystem.server.repository.temperature.TemperatureRepository

/**
 * A transaction for the repositories
 */
interface Transaction {

	// other repository types
	val temperatureRepository: TemperatureRepository

	/**
	 * Rolls back the transaction
	 */
	fun rollback()
}