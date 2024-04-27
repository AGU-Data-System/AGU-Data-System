package aguDataSystem.server.repository.provider

import aguDataSystem.server.domain.AGU
import aguDataSystem.server.domain.Provider
import aguDataSystem.server.domain.Reading

/**
 * A repository for the providers
 */
interface ProviderRepository {

	/**
	 * Gets a provider by its id
	 *
	 * @param id the id of the provider
	 */
	fun getProviderById(id: Int): Provider?

	/**
	 * Gets all the providers
	 */
	fun getAllProviders(): List<Provider>

	/**
	 * Saves a provider for a given AGU
	 *
	 * @param provider the provider to save
	 * @param agu the AGU to save the provider for
	 */
	fun saveProvider(provider: Provider, agu: AGU)

	/**
	 * Deletes a provider by its id and all its readings from a given AGU
	 *
	 * @param id the id of the provider
	 * @param agu the AGU to delete the provider from
	 */
	fun deleteProviderById(id: Int, agu: AGU)

	/**
	 * Gets the last reading of a provider
	 *
	 * @param provider the provider to get the last reading from
	 * @param agu the AGU to get the last reading from
	 * @return the last reading of the provider
	 */
	fun getLastReading(provider: Provider, agu: AGU): Reading

	/**
	 * Gets all the readings of a provider
	 *
	 * @param provider the provider to get the readings from
	 * @param agu the AGU to get the readings from
	 * @return the readings of the provider
	 */
	fun getReadings(provider: Provider, agu: AGU): List<Reading>
}