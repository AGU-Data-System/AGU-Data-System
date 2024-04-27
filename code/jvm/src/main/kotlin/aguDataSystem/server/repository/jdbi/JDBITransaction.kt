package aguDataSystem.server.repository.jdbi

import aguDataSystem.server.repository.Transaction
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.provider.JDBIProviderRepository
import org.jdbi.v3.core.Handle

/**
 * A JDBI implementation of [Transaction]
 * @see Transaction
 * @see Handle
 */
class JDBITransaction(private val handle: Handle) : Transaction {

	override val aguRepository = JDBIAGURepository(handle)

	override val providerRepository = JDBIProviderRepository(handle)

	/**
	 * Rolls back the transaction
	 */
	override fun rollback() {
		handle.rollback()
	}
}