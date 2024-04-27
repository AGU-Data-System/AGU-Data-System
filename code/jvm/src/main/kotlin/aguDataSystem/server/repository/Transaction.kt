package aguDataSystem.server.repository

import aguDataSystem.server.repository.agu.AGURepository
import aguDataSystem.server.repository.provider.ProviderRepository

/**
 * A transaction for the repositories
 */
interface Transaction {

	// other repository types
	val aguRepository: AGURepository

	val providerRepository: ProviderRepository

	/**
	 * Rolls back the transaction
	 */
	fun rollback()
}