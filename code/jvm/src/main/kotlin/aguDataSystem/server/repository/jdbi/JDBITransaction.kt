package aguDataSystem.server.repository.jdbi

import aguDataSystem.server.repository.Transaction
import aguDataSystem.server.repository.temperature.JDBITemperatureRepository
import aguDataSystem.server.repository.temperature.TemperatureRepository
import org.jdbi.v3.core.Handle

/**
 * A JDBI implementation of [Transaction]
 * @see Transaction
 * @see Handle
 */
class JDBITransaction(private val handle: Handle) : Transaction {

	override val temperatureRepository: TemperatureRepository = JDBITemperatureRepository(handle)

	/**
	 * Rolls back the transaction
	 */
	override fun rollback() {
		handle.rollback()
	}
}