import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

@Serializable
data class Provider(
    val name: String,
    val url: String,
    val frequency: String,
    val isActive: Boolean
)

fun main() {
    runBlocking {
        val scraper = DataScraper()
        scraper.fetchAndPostSynoptics()
    }
}

class DataScraper {
    private val sonorgasUrl = "https://dourogas.thinkdigital.pt/dashboards/ca824027-c206-44b9-af54-cba5dc6edde7/viewer"
    private val fetcherUrl = "http://10.64.13.59:8080/api/provider"
    private val client = HttpClient.newHttpClient()
    private val jsonFormatter = Json { prettyPrint = true }

    fun fetchAndPostSynoptics() {

        val doc = Jsoup.connect(sonorgasUrl).get()
        val rows = doc.select("#list .synoptic-list .row-head")

        rows.forEach { row ->
            val id = row.select("td[data-synoptic]").attr("data-synoptic")
            val name = row.select("td[data-synoptic]").text()

            val providerUrl = "$sonorgasUrl/$id/sensors"
            val provider = Provider(name, providerUrl, "PT1H", true)
            sendPostRequest(provider)
        }
    }

    private fun sendPostRequest(provider: Provider) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(fetcherUrl))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(jsonFormatter.encodeToString(provider)))
            .build()

        try {
            println("Sending POST request to $fetcherUrl")
            println("Request body: ${jsonFormatter.encodeToString(provider)}")
            val response = client.send(request, BodyHandlers.ofString())
            println("Response status code: ${response.statusCode()}")
            println("Response body: ${response.body()}")
        } catch (e: Exception) {
            println("Error sending POST request: ${e.message}")
        }
    }
}
