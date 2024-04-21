package aguDataSystem.server.repository

/**
 * A transaction for the repositories
 */
interface Transaction {

	// other repository types

	/**
	 * Rolls back the transaction
	 */
	fun rollback()
}