package aguDataSystem.utils

import aguDataSystem.server.Environment
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.postgresql.ds.PGSimpleDataSource

//TODO: Remove this later, as this is not going to production
@Serializable
data class Provider(
	val id: Int,
	val name: String,
	val url: String,
	val frequency: String,
	val isActive: Boolean,
	val lastFetch: String
)

@Serializable
data class ProvidersResponse(
	val providers: List<Provider>,
	val size: Int
)

interface TransactionManager {
	fun <R> run(block: (Transaction) -> R): R
}

class JDBITransactionManager(private val jdbi: Jdbi) : TransactionManager {
	override fun <R> run(block: (Transaction) -> R): R =
		jdbi.inTransaction<R, Exception> { handle ->
			block(JDBITransaction(handle))
		}
}

interface Transaction {
	fun rollback()
	val handle: Handle
}

class JDBITransaction(override val handle: Handle) : Transaction {
	override fun rollback() {
		handle.rollback()
	}
}

fun main() {
	runBlocking {
		val dataSource = PGSimpleDataSource().apply {
			setURL(Environment.getDbUrl())
		}
		val jdbi = Jdbi.create(dataSource)
		val transactionManager = JDBITransactionManager(jdbi)

		val scraper = ProviderScraper(transactionManager)
		scraper.fetchProviders()
	}
}

class ProviderScraper(private val transactionManager: TransactionManager) {
	private val fetcherUrl = "http://localhost:8081/api/providers"
	private val client = HttpClient.newHttpClient()
	private val jsonFormatter = Json { ignoreUnknownKeys = true; prettyPrint = true }

	fun fetchProviders() {
		val request = HttpRequest.newBuilder()
			.uri(URI.create(fetcherUrl))
			.GET()
			.build()

		val response = client.send(request, BodyHandlers.ofString())
		val providersResponse = jsonFormatter.decodeFromString<ProvidersResponse>(response.body())
		val providersList = providersResponse.providers

		transactionManager.run { tx ->
			providersList.filter { it.name.startsWith("temperature") || it.name.startsWith("gas") }
				.forEach { provider ->
					val aguName = provider.name.substringAfterLast(" - ")
					val cui = fetchCuiFromAguName(tx.handle, aguName)
					if (cui != null) {
						insertProvider(tx.handle, provider, cui)
						println("Provider $provider inserted successfully")
					} else {
						println("AGU $aguName not found in the database")
					}
				}
		}
	}

	private fun fetchCuiFromAguName(handle: Handle, aguName: String): String? =
		handle.createQuery("SELECT cui FROM agu WHERE name = :name")
			.bind("name", aguName)
			.mapTo<String>()
			.firstOrNull()

	private fun insertProvider(handle: Handle, provider: Provider, cui: String) {
		handle.execute(
			"INSERT INTO provider (id, agu_cui, provider_type) VALUES (?, ?, ?)",
			provider.id, cui, if (provider.name.startsWith("temperature")) "temperature" else "gas"
		)
	}
}
