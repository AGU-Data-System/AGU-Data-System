package aguDataSystem.server.repository

import aguDataSystem.server.repository.agu.AGURepository

/**
 * A transaction for the repositories
 */
interface Transaction {

	// other repository types
	val aguRepository: AGURepository

	/**
	 * Rolls back the transaction
	 */
	fun rollback()
}