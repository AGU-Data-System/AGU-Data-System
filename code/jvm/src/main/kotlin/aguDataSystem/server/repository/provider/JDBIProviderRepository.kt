package aguDataSystem.server.repository.provider

import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.domain.provider.GasProvider
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.domain.provider.TemperatureProvider
import java.time.LocalDateTime
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
			SELECT provider.id, provider.agu_cui, provider.provider_type, provider.last_fetch
			FROM provider
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
			SELECT provider.id, provider.agu_cui, provider.provider_type, provider.last_fetch 
			FROM provider
			WHERE provider.agu_cui = :CUI AND provider.provider_type = :providerType
			""".trimIndent()
		)
			.bind("CUI", aguCUI)
			.bind("providerType", providerType.name.lowercase())
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
	 * Gets all the providers of a given AGU
	 *
	 * @param aguCUI the CUI of the AGU
	 * @return a list of all the providers of the AGU
	 */
	override fun getProviderByAGU(aguCUI: String): List<Provider> {
		logger.info("Getting provider with CUI {}", aguCUI)

		val providers = handle.createQuery(
			"""
			SELECT provider.id, provider.agu_cui, provider.provider_type, provider.last_fetch 
			FROM provider
			WHERE provider.agu_cui = :CUI
			""".trimIndent()
		)
			.bind("CUI", aguCUI)
			.mapTo<Provider>()
			.list()

		if (providers.isNotEmpty()) {
			logger.info("Fetched provider with CUI {}", aguCUI)
		} else {
			logger.info("Provider with CUI {} not found", aguCUI)
		}

		return providers
	}

	/**
	 * Gets all the providers
	 *
	 * @return a list of all the providers
	 */
	override fun getAllProviders(): List<Provider> {

		logger.info("Getting all providers")

		val providers = mutableListOf<Provider>()
		ProviderType.entries.forEach {
			when (it) {
				ProviderType.TEMPERATURE -> providers.addAll(getAllTemperatureProviders())
				ProviderType.GAS -> providers.addAll(getAllGasProviders())
			}
		}

		logger.info("Fetched {} total providers", providers.size)

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

		val pId = handle.createUpdate(
			"""
			INSERT INTO provider (id, agu_cui, provider_type) VALUES (:providerId, :cui, :providerType)
			""".trimIndent()
		)
			.bind("cui", cui)
			.bind("providerId", providerId)
			.bind("providerType", providerType.name.lowercase())
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

	/**
	 * Updates the last fetch time of a provider
	 *
	 * @param id the id of the provider
	 * @param lastFetch the last fetch time
	 */
	override fun updateLastFetch(id: Int, lastFetch: LocalDateTime) {
		logger.info("Updating last fetch time of provider with id {}", id)

		val updates = handle.createUpdate(
			"""
			UPDATE provider SET last_fetch = :lastFetch WHERE id = :id
			""".trimIndent()
		)
			.bind("id", id)
			.bind("lastFetch", lastFetch)
			.execute()

		logger.info("last fetch time updated to {} in provider with id {}", updates, id)

		if (updates == 0) {
			logger.info("Provider with id {} not found, didn't update last fetch", id)
		} else {
			logger.info("Updated last fetch time of provider with id {}", id)
		}
	}

	/**
	 * Gets the AGU CUI of a provider
	 *
	 * @param providerId the id of the provider
	 * @return the CUI of the AGU
	 */
	override fun getAGUCuiFromProviderId(providerId: Int): String? {
		logger.info("Getting AGU CUI of provider with id {}", providerId)

		val aguCUI = handle.createQuery(
			"""
			SELECT agu_cui FROM provider WHERE id = :id
			""".trimIndent()
		)
			.bind("id", providerId)
			.mapTo<String>()
			.singleOrNull()

		if (aguCUI != null) {
			logger.info("Fetched AGU CUI of provider with id {}: {}", providerId, aguCUI)
		} else {
			logger.info("Provider with id {} not found, could not find agu cui", providerId)
		}

		return aguCUI
	}

	/**
	 * Gets all the temperature providers with their measures.
	 *
	 * @return a list of all the temperature providers
	 */
	private fun getAllTemperatureProviders(): List<Provider> {
		logger.info("Getting all temperature providers")

		val providers = handle.createQuery(
			"""
			SELECT provider.id, provider.provider_type, provider.last_fetch
			FROM provider
			WHERE provider.provider_type = :providerType
			Order BY provider.id
			""".trimIndent()
		)
			.bind("providerType", ProviderType.TEMPERATURE.name.lowercase())
			.bind("minTag", TemperatureMeasure::min.name)
			.bind("maxTag", TemperatureMeasure::max.name)
			.mapTo<TemperatureProvider>()
			.list()

		logger.info("Fetched {} temperature providers", providers.size)

		return providers
	}

	/**
	 * Gets all the gas providers with their measures.
	 *
	 * @return a list of all the gas providers
	 */
	private fun getAllGasProviders(): List<GasProvider> {
		logger.info("Getting all gas providers")

		val providers = handle.createQuery(
			"""
			SELECT provider.id, provider.agu_cui, provider.provider_type, provider.last_fetch
			FROM provider
		 	WHERE provider.provider_type = :providerType
			Order BY provider.id
			""".trimIndent()
		)
			.bind("providerType", ProviderType.GAS.name.lowercase())
			.mapTo<GasProvider>()
			.list()

		logger.info("Fetched {} gas providers", providers.size)

		return providers

	}

	companion object {
		private val logger = LoggerFactory.getLogger(JDBIProviderRepository::class.java)
	}
}
