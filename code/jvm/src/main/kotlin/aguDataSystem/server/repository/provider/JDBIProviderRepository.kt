package aguDataSystem.server.repository.provider

import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
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
	 * @return the provider with the given id or null if it doesn't exist
	 */
	override fun getProviderById(id: Int): Provider? {
		logger.info("Getting provider with id {}", id)

		val provider = handle.createQuery(
			"""
			SELECT provider.id, provider.agu_cui, provider.provider_type, measure.timestamp, measure.prediction_for, measure.tag, measure.data FROM provider
			left join measure on provider.id = measure.provider_id
			WHERE provider.id = :id
			""".trimIndent()
		)
			.bind("id", id)
			.mapTo<Provider>()
			.singleOrNull()

		if (provider != null) {
			logger.info("Fetched provider with id: {}", id)
		} else {
			logger.info("Provider with id {} not found", id)
		}

		return provider
	}

	/**
	 * Gets a provider by the AGU it belongs to and its type
	 *
	 * @param aguCUI the CUI of the AGU
	 * @param providerType the type of the provider
	 * @return the provider with the given AGU and type or null if it doesn't exist
	 */
	override fun getProviderByAGUAndType(aguCUI: String, providerType: ProviderType): Provider? {
		logger.info("Getting provider with CUI {} and type {}", aguCUI, providerType)

		val provider = handle.createQuery(
			"""
			SELECT provider.id, provider.agu_cui, provider.provider_type FROM provider
			WHERE provider.agu_cui = :CUI AND provider.provider_type = :providerType
			""".trimIndent()
		)
			.bind("CUI", aguCUI)
			.bind("providerType", providerType.name)
			.mapTo<Provider>()
			.singleOrNull()

		if (provider != null) {
			logger.info("Fetched provider with CUI {} and type {}", aguCUI, providerType)
		} else {
			logger.info("Provider with CUI {} and type {} not found", aguCUI, providerType)
		}

		return provider
	}

	/**
	 * Gets all the providers
	 *
	 * @return a list of all the providers
	 */
//	override fun getAllProviders(): List<Provider> {
//
//		logger.info("Getting all providers")
//
//		val providers = handle.createQuery(
//			"""
//			SELECT provider.id, provider.agu_cui, provider.provider_type,
//			measure.timestamp, measure.prediction_for, measure.tag, measure.data
//			FROM provider
//			left join measure on provider.id = measure.provider_id
//			Order BY provider.id, measure.timestamp, measure.prediction_for, measure.tag, measure.data
//			""".trimIndent()
//		)
//			.mapTo<Provider>()
//			.list()
//
//		logger.info("Fetched {} providers", providers.size)
//
//		return providers
//	}

	/**
	 * Saves a provider for a given AGU
	 *
	 * @param cui the cui of the AGU
	 * @param providerId the id of the provider
	 * @param providerType the type of the provider
	 */
	override fun addProvider(cui: String, providerId: Int, providerType: ProviderType) {
		logger.info("Adding provider with id {} to AGU with cui {}", providerId, cui)

		val pId = handle.createUpdate(
			"""
			INSERT INTO provider (id, agu_cui, provider_type) VALUES (:providerId, :cui, :providerType)
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("providerId", providerId)
			.bind("providerType", providerType.name)
			.executeAndReturnGeneratedKeys(Provider::id.name)
			.mapTo<Int>()
			.one()

		logger.info("Added provider with id {} to AGU with cui {}", pId, cui)
	}

	/**
	 * Deletes a provider by its id and all its readings from a given AGU
	 *
	 * @param id the id of the provider
	 * @param cui the AGU to delete the provider from
	 */
	override fun deleteProviderById(id: Int, cui: String) {
		logger.info("Deleting provider with id {} from AGU with cui {}", id, cui)

		val deletions = handle.createUpdate(
			"""
			DELETE FROM provider WHERE id = :id AND agu_cui = :cui
			""".trimIndent()
		)
			.bind("id", id)
			.bind("cui", cui)
			.execute()

		if (deletions == 0) {
			logger.info("Provider with id {} not found in AGU with cui {}", id, cui)
		} else {
			logger.info("Deleted provider with id {} from AGU with cui {}", id, cui)
		}
	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIProviderRepository::class.java)
	}
}
