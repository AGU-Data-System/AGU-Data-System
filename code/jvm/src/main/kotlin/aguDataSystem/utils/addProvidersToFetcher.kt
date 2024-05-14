package aguDataSystem.utils

import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

//TODO: Remove this later, as this is not going to production
@Serializable
data class ProviderInput(
	val name: String,
	val url: String,
	val frequency: String,
	val isActive: Boolean
)

fun main() {
	runBlocking {
		val scraper = GasAndTempScraper()
		scraper.fetchAndPostGasSynoptics()
		scraper.fetchAndPostTemperatureSynoptics("code/jvm/src/main/kotlin/aguDataSystem/utils/12042024_UAGs_Route_Map.csv")
	}
}

class GasAndTempScraper {
	private val sonorgasUrl = "https://dourogas.thinkdigital.pt/dashboards/ca824027-c206-44b9-af54-cba5dc6edde7/viewer"
	private val fetcherUrl = "http://localhost:8081/api/provider"
	private val client = HttpClient.newHttpClient()
	private val jsonFormatter = Json { prettyPrint = true }

	fun fetchAndPostGasSynoptics() {

		val doc = Jsoup.connect(sonorgasUrl).get()
		val rows = doc.select("#list .synoptic-list .row-head")

		rows.forEach { row ->
			val id = row.select("td[data-synoptic]").attr("data-synoptic")
			val name = row.select("td[data-synoptic]").text().substringAfterLast("-").trim()
			val fullName = "gas - $name"

			val providerUrl = "$sonorgasUrl/$id/sensors"
			val providerInput = ProviderInput(fullName, providerUrl, "PT1H", true)
			sendPostRequest(providerInput)
		}
	}

	fun fetchAndPostTemperatureSynoptics(csvPath: String) {
		val rows = csvReader {
			charset = "UTF-16"
			this.insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
			autoRenameDuplicateHeaders = true
		}.readAllWithHeader(File(csvPath))

		rows.forEach { row ->
			val latitude = row["Latitude"]
			val longitude = row["Longitude"]
			val aguName = row["UAG"]

			if (latitude.isNullOrEmpty() && longitude.isNullOrEmpty() && aguName.isNullOrEmpty()) {
				println("Encountered the first empty row; stopping processing.")
				return
			}

			if (!latitude.isNullOrEmpty() && !longitude.isNullOrEmpty() && !aguName.isNullOrEmpty()) {
				val url =
					"https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&daily=temperature_2m_max,temperature_2m_min&timezone=Europe%2FLondon&forecast_days=10"
				val providerName = "temperature - $aguName"
				val providerInput = ProviderInput(providerName, url, "P1D", true)

				sendPostRequest(providerInput)
			} else {
				println("Skipping incomplete data for AGU: $aguName")
			}
		}
	}

	private fun sendPostRequest(providerInput: ProviderInput) {
		val request = HttpRequest.newBuilder()
			.uri(URI.create(fetcherUrl))
			.header("Content-Type", "application/json")
			.POST(BodyPublishers.ofString(jsonFormatter.encodeToString(providerInput)))
			.build()

		try {
			println("Sending POST request to $fetcherUrl")
			println("Request body: ${jsonFormatter.encodeToString(providerInput)}")
			val response = client.send(request, BodyHandlers.ofString())
			println("Response status code: ${response.statusCode()}")
			println("Response body: ${response.body()}")
		} catch (e: Exception) {
			println("Error sending POST request: ${e.message}")
		}
	}
}
