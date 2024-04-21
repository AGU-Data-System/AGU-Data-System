import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import java.io.File
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
        scraper.fetchAndPostGasSynoptics()
        scraper.fetchAndPostTemperatureSynoptics("code/jvm/src/main/kotlin/aguDataSystem/utils/12042024_UAGs Route Map.csv")
    }
}

class DataScraper {
    private val sonorgasUrl = "https://dourogas.thinkdigital.pt/dashboards/ca824027-c206-44b9-af54-cba5dc6edde7/viewer"
    private val fetcherUrl = "http://10.64.13.59:8080/api/provider"
    private val client = HttpClient.newHttpClient()
    private val jsonFormatter = Json { prettyPrint = true }

    fun fetchAndPostGasSynoptics() {

        val doc = Jsoup.connect(sonorgasUrl).get()
        val rows = doc.select("#list .synoptic-list .row-head")

        rows.forEach { row ->
            val id = row.select("td[data-synoptic]").attr("data-synoptic")
            val name = "gas - " + row.select("td[data-synoptic]").text()

            val providerUrl = "$sonorgasUrl/$id/sensors"
            val provider = Provider(name, providerUrl, "PT1H", true)
            sendPostRequest(provider)
        }
    }

    fun fetchAndPostTemperatureSynoptics(csvPath: String) {
        val rows = csvReader {
            autoRenameDuplicateHeaders = true
        }.readAllWithHeader(File(csvPath))

        rows.forEach { row ->
            val latitude = row["Latitude"] ?: error("Latitude not found")
            val longitude = row["Longitude"] ?: error("Longitude not found")
            val aguName = row["UAG"] ?: error("AGU name not found")

            val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&daily=temperature_2m_max,temperature_2m_min&timezone=Europe%2FLondon&forecast_days=10"
            val providerName = "temperature - $aguName"
            val provider = Provider(providerName, url, "P1D", true)

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
