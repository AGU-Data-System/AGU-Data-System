package aguDataSystem.server.repository.jdbi

import aguDataSystem.server.repository.Transaction
import org.jdbi.v3.core.Handle

/**
 * A JDBI implementation of [Transaction]
 * @see Transaction
 * @see Handle
 */
class JDBITransaction(private val handle: Handle) : Transaction {

	/**
	 * Rolls back the transaction
	 */
	override fun rollback() {
		handle.rollback()
	}
}