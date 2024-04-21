import aguDataSystem.server.Environment
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.Handle
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

//TODO: Not too comfortable about this script.
//Should work but it's smelly
//Alternative would be to use a stored procedure and simply make the get for the providers and then call the procedure

@Serializable
data class Provider(
    val id: Int,
    val name: String,
    val url: String,
    val frequency: String,
    val isActive: Boolean,
    val lastFetch: String
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
    private val fetcherUrl = "http://10.64.13.59:8080/api/providers"
    private val client = HttpClient.newHttpClient()
    private val jsonFormatter = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun fetchProviders() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(fetcherUrl))
            .GET()
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val providersList = jsonFormatter.decodeFromString<List<Provider>>(response.body())

        transactionManager.run { tx ->
            providersList.filter { it.name.startsWith("temperature") || it.name.startsWith("gas") }.forEach { provider ->
                val aguName = provider.name.substringAfterLast(" - ")
                val cui = fetchCuiFromAguName(tx.handle, aguName)
                if (cui != null) {
                    insertProvider(tx.handle, provider, cui)
                }
            }
        }
    }

    private fun fetchCuiFromAguName(handle: Handle, aguName: String): String? =
        handle.createQuery("SELECT cui FROM agu WHERE name = :name")
            .bind("name", aguName)
            .mapTo(String::class.java)
            .firstOrNull()

    private fun insertProvider(handle: Handle, provider: Provider, cui: String) {
        handle.execute("INSERT INTO provider (id, agu_cui, provider_type) VALUES (?, ?, ?)",
            provider.id, cui, if (provider.name.startsWith("temperature")) "temperature" else "gas")
    }
}
