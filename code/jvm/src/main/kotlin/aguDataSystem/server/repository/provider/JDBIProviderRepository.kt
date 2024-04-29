package aguDataSystem.server.repository.provider

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.reading.Reading
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [ProviderRepository]
 * @see ProviderRepository
 * @see Handle
 */
class JDBIProviderRepository(private val handle: Handle) : ProviderRepository {
	/**
	 * Gets a provider by its id
	 *
	 * @param id the id of the provider
	 */
	override fun getProviderById(id: Int): Provider? {
		TODO("Not yet implemented")
	}

	/**
	 * Gets all the providers
	 */
	override fun getAllProviders(): List<Provider> {

		logger.info("Getting all providers")

		val providers = handle.createQuery(
			"""
			SELECT provider.id, provider.agu_cui, provider.provider_type, measure.timestamp, measure.prediction_for, measure.tag, measure.data FROM provider
			join measure on provider.id = measure.provider_id
			GROUP BY provider.id
			""".trimIndent()
		)
			.mapTo<Provider>()
			.list()

		logger.info("Fetched {} providers", providers.size)

		return providers
	}

	/**
	 * Saves a provider for a given AGU
	 *
	 * @param provider the provider to save
	 * @param agu the AGU to save the provider for
	 */
	override fun saveProvider(provider: Provider, agu: AGU) {
		TODO("Not yet implemented")
	}

	/**
	 * Deletes a provider by its id and all its readings from a given AGU
	 *
	 * @param id the id of the provider
	 * @param agu the AGU to delete the provider from
	 */
	override fun deleteProviderById(id: Int, agu: AGU) {
		TODO("Not yet implemented")
	}

	/**
	 * Gets the last reading of a provider
	 *
	 * @param provider the provider to get the last reading from
	 * @param agu the AGU to get the last reading from
	 * @return the last reading of the provider
	 */
	override fun getLastReading(provider: Provider, agu: AGU): Reading {
		TODO("Not yet implemented")
	}

	/**
	 * Gets all the readings of a provider
	 *
	 * @param provider the provider to get the readings from
	 * @param agu the AGU to get the readings from
	 * @return the readings of the provider
	 */
	override fun getReadings(provider: Provider, agu: AGU): List<Reading> {
		TODO("Not yet implemented")
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIProviderRepository::class.java)
	}
}