package aguDataSystem.server.repository.provider

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
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
		logger.info("Getting provider with id {}", id)

		val provider = handle.createQuery(
			"""
			SELECT provider.id, provider.agu_cui, provider.provider_type, measure.timestamp, measure.prediction_for, measure.tag, measure.data FROM provider
			join measure on provider.id = measure.provider_id
			WHERE provider.id = :id
			""".trimIndent()
		)
			.bind("id", id)
			.mapTo<Provider>()
			.one()

		logger.info("Fetched provider with id: {}", id)

		return provider
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
	 * @param cui the cui of the AGU
	 * @param providerId the id of the provider
	 * @param providerType the type of the provider
	 */
	override fun addProvider(cui: String, providerId: Int, providerType: ProviderType) {
		logger.info("Adding provider with id {} to AGU with cui {}", providerId, cui)

		handle.createUpdate(
			"""
			INSERT INTO provider (agu_cui, id, provider_type) VALUES (:cui, :providerId, :providerType)
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("providerId", providerId)
			.bind("providerType", providerType.name)
			.execute()

		logger.info("Added provider with id {} to AGU with cui {}", providerId, cui)
	}

	/**
	 * Deletes a provider by its id and all its readings from a given AGU
	 *
	 * @param id the id of the provider
	 * @param agu the AGU to delete the provider from
	 */
	override fun deleteProviderById(id: Int, agu: AGU) {
		logger.info("Deleting provider with id {} from AGU with cui {}", id, agu.cui)

		handle.createUpdate(
			"""
			DELETE FROM provider WHERE id = :id AND agu_cui = :cui
			""".trimIndent()
		)
			.bind("id", id)
			.bind("cui", agu.cui)
			.execute()

		logger.info("Deleted provider with id {} from AGU with cui {}", id, agu.cui)
	}

	/**
	 * Gets the last reading of a provider
	 * TODO needs rethinking because provider already has readings
	 * TODO cant use limit cause of temp(2providers) and gas(1provider)
	 * @param provider the provider to get the last reading from
	 * @param agu the AGU to get the last reading from
	 * @return the last reading of the provider
	 */
	override fun getLatestReading(provider: Provider, agu: AGU): Reading {
		logger.info("Getting last reading of provider with id {} from AGU with cui {}", provider.id, agu.cui)

		val reading = handle.createQuery(
			"""
			SELECT provider.id, provider.agu_cui, provider.provider_type, measure.timestamp, measure.prediction_for, measure.tag, measure.data FROM provider
			join measure on provider.id = measure.provider_id
			WHERE provider.id = :id
			ORDER BY measure.timestamp DESC
			LIMIT 1
			""".trimIndent()
		)
			.bind("id", provider.id)
			.mapTo<Reading>()
			.one()

		logger.info("Fetched last reading of provider with id {} from AGU with cui {}", provider.id, agu.cui)

		return reading
	}

	/**
	 * Gets all the readings of a provider
	 * TODO needs rethinking because provider already has readings
	 * @param provider the provider to get the readings from
	 * @param agu the AGU to get the readings from
	 * @return the readings of the provider
	 */
	override fun getReadings(provider: Provider, agu: AGU): List<Reading> {
		logger.info("Getting readings of provider with id {} from AGU with cui {}", provider.id, agu.cui)

		val readings = handle.createQuery(
			"""
			SELECT provider.id, provider.agu_cui, provider.provider_type, measure.timestamp, measure.prediction_for, measure.tag, measure.data FROM provider
			join measure on provider.id = measure.provider_id
			WHERE provider.id = :id
			""".trimIndent()
		)
			.bind("id", provider.id)
			.mapTo<Reading>()
			.list()

		logger.info("Fetched readings of provider with id {} from AGU with cui {}", provider.id, agu.cui)

		return readings
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIProviderRepository::class.java)
	}
}