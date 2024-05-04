package aguDataSystem.server.repository.provider

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.domain.measure.Measure

/**
 * A repository for the providers
 */
interface ProviderRepository {

	/**
	 * Gets a provider by its id
	 *
	 * @param id the id of the provider
	 * @return the provider with the given id or null if it doesn't exist
	 */
	fun getProviderById(id: Int): Provider?

	/**
	 * Gets a provider by the AGU it belongs to and its type
	 *
	 * @param aguCUI the CUI of the AGU
	 * @param providerType the type of the provider
	 * @return the provider with the given AGU and type or null if it doesn't exist
	 */
	fun getProviderByAGUAndType(aguCUI: String, providerType: ProviderType): Provider?

	/**
	 * Gets all the providers
	 *
	 * @return a list of all the providers
	 */
	fun getAllProviders(): List<Provider>

	/**
	 * Saves a provider for a given AGU
	 *
	 * @param cui the cui of the AGU
	 * @param providerId the id of the provider
	 * @param providerType the type of the provider
	 */
	fun addProvider(cui: String, providerId: Int, providerType: ProviderType)

	/**
	 * Deletes a provider by its id and all its readings from a given AGU
	 *
	 * @param id the id of the provider
	 * @param agu the AGU to delete the provider from
	 */
	fun deleteProviderById(id: Int, agu: AGU)

	/**
	 * Gets the latest reading of a provider
	 *
	 * @param provider the provider to get the last reading from
	 * @param agu the AGU to get the last reading from
	 * @return the last reading of the provider
	 */
	fun getLatestReading(provider: Provider, agu: AGU): Measure

	/**
	 * Gets all the readings of a provider
	 *
	 * @param provider the provider to get the readings from
	 * @param agu the AGU to get the readings from
	 * @return the readings of the provider
	 */
	fun getReadings(provider: Provider, agu: AGU): List<Measure>
}