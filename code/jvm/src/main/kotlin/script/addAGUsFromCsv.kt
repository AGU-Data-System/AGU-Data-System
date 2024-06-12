package script

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
import org.slf4j.LoggerFactory

@Serializable
data class AddAGUInputModel(
	val cui: String,
	val eic: String,
	val name: String,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val loadVolume: Double,
	val correctionFactor: Double,
	val latitude: Double,
	val longitude: Double,
	val locationName: String,
	val dnoName: String,
	val gasLevelUrl: String?,
	val image: ByteArray?,
	val tanks: List<TankCreationInputModel> = emptyList(),
	val contacts: List<ContactCreationInputModel> = emptyList(),
	val transportCompanies: List<String> = emptyList(),
	val isFavorite: Boolean,
	val isActive: Boolean,
	val notes: String?,
)

@Serializable
data class TankCreationInputModel(
	val number: Int,
	val minLevel: Int,
	val criticalLevel: Int,
	val maxLevel: Int,
	val correctionFactor: Double,
	val capacity: Double
)

@Serializable
data class ContactCreationInputModel(
	val name: String,
	val phone: String,
	val type: String
)

fun main() {
	runBlocking {
		val aguProcessor = AGUProcessor()
		aguProcessor.processCSV("code/jvm/src/main/kotlin/script/UAGS GL UAG ISEL 27.05.csv") // TODO: Change to actual path
	}
}

class AGUProcessor {
	private val apiUrl = "http://localhost:8080/api/agus/create"
	private val client = HttpClient.newHttpClient()
	private val jsonFormatter = Json { prettyPrint = true }
	private val logger = LoggerFactory.getLogger(AGUProcessor::class.java)
	private val sonorgasUrl = "https://sonorgas.thinkdigital.pt/dashboards/ca824027-c206-44b9-af54-cba5dc6edde7/viewer"

	fun processCSV(csvPath: String) {
		val rows = csvReader {
			charset = "UTF-8"
			this.insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
			autoRenameDuplicateHeaders = true
		}.readAllWithHeader(File(csvPath))

		// Fetch gas URLs
		val gasUrls = fetchGasUrls()

		rows.forEach { row ->

			try {
				if (row["Nome ORD"] != "SNG") {
					logger.info("Skipping AGU from non-SNG ORD: ${row["Nome UAG"]}")
					return@forEach
				}

				val transportCompanies = extractTransportCompanies(row)
				val tanks = extractTanks(row)
				val contacts = extractContacts(row)
				val name = row["Nome UAG"]!!
				val gasLevelUrl = gasUrls.entries.find { it.key.contains(name, ignoreCase = true) }?.value

				val aguInput = AddAGUInputModel(
					cui = row["CUI"]!!,
					eic = row["EIC"]!!,
					name = name,
					minLevel = row["UAG Min Lvl"]!!.toPercentageInt(),
					maxLevel = row["UAG Max Lvl"]!!.toPercentageInt(),
					criticalLevel = row["UAG Crit Lvl min"]!!.toPercentageInt(),
					loadVolume = row["% carga no Vol Total (cist. 20 ton)"]!!.toDouble(),
					latitude = row["Latitude"]!!.toDouble(),
					longitude = row["Longitude"]!!.toDouble(),
					locationName = row["Localidade"]!!,
					dnoName = row["Nome ORD"]!!,
					gasLevelUrl = gasLevelUrl,
					image = null,
					transportCompanies = transportCompanies,
					notes = row["Observações"],
					correctionFactor = row["UAG Fator de Correção"]!!.toDouble(),
					tanks = tanks,
					contacts = contacts,
					isActive = row["Activo/ Inactivo"] == "Activo",
					isFavorite = false
				)
				logger.info("Processing input model: $aguInput")
				sendPostRequest(aguInput)
			} catch (e: Exception) {
				logger.error("Error processing row: $row", e)
			}
		}
	}

	private fun fetchGasUrls(): Map<String, String> {
		val gasUrls = mutableMapOf<String, String>()
		try {
			val doc = Jsoup.connect(sonorgasUrl).get()
			val rows = doc.select("#list .synoptic-list .row-head")

			rows.forEach { row ->
				val id = row.select("td[data-synoptic]").attr("data-synoptic")
				var name = row.select("td[data-synoptic]").text().substringAfter("-").trim()

				if (name.startsWith("UAG ")) {
					name = name.removePrefix("UAG ").trim()
				}

				val gasUrl = "$sonorgasUrl/$id/sensors"
				gasUrls[name] = gasUrl
			}
		} catch (e: Exception) {
			logger.error("Error fetching gas URLs: ${e.message}")
		}
		return gasUrls
	}

	private fun sendPostRequest(aguInput: AddAGUInputModel) {
		val body = jsonFormatter.encodeToString(aguInput)
		logger.info("Sending POST request to $apiUrl with body: $body")
		val request = HttpRequest.newBuilder()
			.uri(URI.create(apiUrl))
			.header("Content-Type", "application/json")
			.POST(BodyPublishers.ofString(body))
			.build()

		try {
			val response = client.send(request, BodyHandlers.ofString())
			logger.info("Response status code: ${response.statusCode()}")
			logger.info("Response body: ${response.body()}")
		} catch (e: Exception) {
			logger.error("Error sending POST request: ${e.message}")
		}
	}

	private fun extractTransportCompanies(row: Map<String, String>): List<String> {
		return row.filter { it.key.startsWith("Transportador ") && it.value.toBoolean() }
			.map { it.key.removePrefix("Transportador ").trim() }
	}

	private fun extractTanks(row: Map<String, String>): List<TankCreationInputModel> {
		return (1..3).mapNotNull { i ->
			if (row["Volume Tk$i (m3) "] == "-") null
			else TankCreationInputModel(
				number = i,
				minLevel = row["UAG Min Lvl Tk$i"]!!.toPercentageInt(),
				criticalLevel = row["UAG Crit Lvl min Tk$i"]!!.toPercentageInt(),
				maxLevel = row["UAG Max Lvl Tk$i"]!!.toPercentageInt(),
				correctionFactor = row["UAG Fator de Correção Tk$i"]!!.toDouble(),
				capacity = row["Volume Tk$i (m3) "]!!.toDouble()
			)
		}
	}

	private fun extractContact(
		nameField: String,
		phoneField: String,
		type: String,
		row: Map<String, String>
	): ContactCreationInputModel? {
		val name = row[nameField]
		val phone = row[phoneField]
		return if (name != null && name != "-" && phone != null && phone != "-") {
			ContactCreationInputModel(name = name.trim(), phone = phone.filter { !it.isWhitespace() }, type = type)
		} else {
			null
		}
	}


	private fun extractContacts(row: Map<String, String>): List<ContactCreationInputModel> {
		return listOfNotNull(
			extractContact("Nome Contacto Logistica", "Nº Contacto Logistica", "logistic", row),
			extractContact("Nome Contacto Emergencia ", "Nº Contacto  Emergencia ", "emergency", row),
			extractContact("Nome substituto ", "Nº Contacto  Emergencia substituto", "emergency", row)
		)
	}


	private fun String.toPercentageInt(): Int {
		return this.removeSuffix("%").toDouble().toInt()
	}
}
