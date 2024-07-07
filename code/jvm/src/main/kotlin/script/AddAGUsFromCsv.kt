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
	val isFavourite: Boolean,
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

/**
 * Environment variables
 */
object Environment {

	/**
	 * Get the CSV path to add data to the main system
	 */
	fun getCSVPath() = System.getenv(KEY_CSV_PATH) ?: throw Exception("Missing env var $KEY_CSV_PATH")

	/**
	 * Get the API URL to communicate with the api to send data
	 */
	fun getApiUrl() = System.getenv(KEY_API_URL) ?: throw Exception("Missing env var $KEY_API_URL")

	/**
	 * Get the SonarGas URL to fetch urls for each elem.
	 */
	fun getSonarGasUrl() = System.getenv(KEY_SONAR_GAS_URL) ?: throw Exception("Missing env var $KEY_SONAR_GAS_URL")

	private const val KEY_CSV_PATH = "CSV_PATH"
	private const val KEY_API_URL = "API_URL"
	private const val KEY_SONAR_GAS_URL = "SONAR_GAS_URL"
}

/**
 * Program entry point
 */
fun main() {
	val csvPath = Environment.getCSVPath() //"code/jvm/src/main/kotlin/script/script_values.csv"
	runBlocking {
		val aguProcessor = AGUProcessor()
		aguProcessor.processCSV(csvPath = csvPath)
	}
}

/**
 * Manipulates data from a CSV and sends requests to a system to save such data
 */
class AGUProcessor {
	private val apiUrl = Environment.getApiUrl() //"http://localhost:8080/api/agus/create"
	private val client = HttpClient.newHttpClient()
	private val jsonFormatter = Json { prettyPrint = true }
	private val logger = LoggerFactory.getLogger(AGUProcessor::class.java)
	private val sonarGasUrl =
		Environment.getSonarGasUrl() //"https://sonorgas.thinkdigital.pt/dashboards/ca824027-c206-44b9-af54-cba5dc6edde7/viewer"

	// Constants
	private val cui = "CUI"
	private val eic = "EIC"
	private val aguName = "Nome UAG"
	private val aguMinLevel = "UAG Min Lvl"
	private val aguMaxLevel = "UAG Max Lvl"
	private val aguCriticalLevel = "UAG Crit Lvl min"
	private val latitude = "Latitude"
	private val longitude = "Longitude"
	private val town = "Localidade"
	private val dnoName = "Nome ORD"
	private val notes = "Observações"
	private val correctionFactor = "UAG Fator de Correção"
	private val isActive = "Activo/ Inactivo"

	private val transportCompany = "Transportador "

	private val logisticContactName = "Nome Contacto Logistica"
	private val logisticContactNumber = "Nº Contacto Logistica"
	private val logisticContactType = "logistic"
	private val emergencyContactName = "Nome Contacto Emergencia "
	private val emergencyContactNumber = "Nº Contacto  Emergencia "
	private val emergencyContactType = "emergency"
	private val emergencyReplacementContactName = "Nome substituto "
	private val emergencyReplacementContactNumber = "Nº Contacto  Emergencia substituto"
	private val emergencyReplacementContactType = "emergency"

	private val aguPrefix = "UAG "

	/**
	 * Processes a CSV into several entities to save in the main system
	 *
	 * @param csvPath the path for the CSV with the needed data
	 */
	fun processCSV(csvPath: String) {
		val rows = csvReader {
			charset = "UTF-8"
			this.insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
			autoRenameDuplicateHeaders = true
		}.readAllWithHeader(File(csvPath))

		// Fetch gas URLs
		val gasUrls = fetchGasUrls(url = sonarGasUrl)

		rows.forEach { row ->

			try {
				if (row[dnoName] != "SNG") {
					logger.info("Skipping AGU from non-SNG ORD: ${row[aguName]}")
					return@forEach
				}

				val transportCompanies = extractTransportCompanies(row = row)
				val tanks = extractTanks(row = row)
				val contacts = extractContacts(row = row)
				val name = row[aguName]!!
				val gasLevelUrl = gasUrls.entries.find { it.key.contains(name, ignoreCase = true) }?.value

				val aguInput = AddAGUInputModel(
					cui = row[cui]!!,
					eic = row[eic]!!,
					name = name,
					minLevel = row[aguMinLevel]!!.toPercentageInt(),
					maxLevel = row[aguMaxLevel]!!.toPercentageInt(),
					criticalLevel = row[aguCriticalLevel]!!.toPercentageInt(),
					latitude = row[latitude]!!.toDouble(),
					longitude = row[longitude]!!.toDouble(),
					locationName = row[town]!!,
					dnoName = row[dnoName]!!,
					gasLevelUrl = gasLevelUrl,
					image = null,
					transportCompanies = transportCompanies,
					notes = row[notes],
					correctionFactor = row[correctionFactor]!!.toDouble(),
					tanks = tanks,
					contacts = contacts,
					isActive = row[isActive] == "Activo",
					isFavourite = false
				)
				logger.info("Processing input model: $aguInput")
				sendPostRequest(aguInput)
			} catch (e: Exception) {
				logger.error("Error processing row: $row", e)
			}
		}
	}

	/**
	 * Fetches A URL for each entity based on an url
	 *
	 * @param url the url to fetch a gas url
	 * @return a Map containing all the gas urls
	 */
	private fun fetchGasUrls(url: String): Map<String, String> {
		val gasUrls = mutableMapOf<String, String>()
		try {
			val doc = Jsoup.connect(url).get()
			val rows = doc.select("#list .synoptic-list .row-head")

			rows.forEach { row ->
				val id = row.select("td[data-synoptic]").attr("data-synoptic")
				var name = row.select("td[data-synoptic]").text().substringAfter("-").trim()

				if (name.startsWith(aguPrefix)) {
					name = name.removePrefix(aguPrefix).trim()
				}

				val gasUrl = "$sonarGasUrl/$id/sensors"
				gasUrls[name] = gasUrl
			}
		} catch (e: Exception) {
			logger.error("Error fetching gas URLs: ${e.message}")
		}
		return gasUrls
	}

	/**
	 * Sends a post-request to create an entity in the main system.
	 *
	 * @param aguInput an object holding the body of the request
	 */
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

	/**
	 * Extracts the Transport Companies from a CSV data source row
	 *
	 * @param row the row to extract data from
	 * @return The list of Transport Companies extracted
	 */
	private fun extractTransportCompanies(row: Map<String, String>): List<String> {
		return row.filter { it.key.startsWith(transportCompany) && it.value.toBoolean() }
			.map { it.key.removePrefix(transportCompany).trim() }
	}

	/**
	 * Serializes a Row into a TankCreationInputModel
	 *
	 * @param row The row of the CSV to serialize
	 * @return a List of TankCreationInputModel
	 */
	private fun extractTanks(row: Map<String, String>): List<TankCreationInputModel> {
		return (1..3).mapNotNull { i ->
			if (row["Volume Tk$i (m3) "].isNullOrBlank()) null
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

	/**
	 * Serializes a row into ContactCreationInputModel
	 *
	 * @param nameField the key for the cell that has the name of the contact
	 * @param phoneField the key for the cell that has the phone number
	 * @param type the key for the cell that has the phone type
	 * @return a Serialized ContactCreationInputModel or null if there's any issue with the format.
	 */
	private fun extractContact(
		nameField: String,
		phoneField: String,
		type: String,
		row: Map<String, String>
	): ContactCreationInputModel? {
		val name = row[nameField]
		val phone = row[phoneField]
		return if (name.isNullOrBlank() || phone.isNullOrBlank())
			null
		else
			ContactCreationInputModel(
				name = name!!.trim(),
				phone = phone!!.filter { !it.isWhitespace() },
				type = type
			)
	}

	/**
	 * Extracts all the contact of the row
	 *
	 * @param row the row to extract the contacts from
	 * @return A list of ContactCreationInputModel
	 */
	private fun extractContacts(row: Map<String, String>): List<ContactCreationInputModel> {
		return listOfNotNull(
			extractContact(
				nameField = logisticContactName,
				phoneField = logisticContactNumber,
				type = logisticContactType,
				row = row
			),
			extractContact(
				nameField = emergencyContactName,
				phoneField = emergencyContactNumber,
				type = emergencyContactType,
				row = row
			),
			extractContact(
				nameField = emergencyReplacementContactName,
				phoneField = emergencyReplacementContactNumber,
				type = emergencyReplacementContactType,
				row = row
			)
		)
	}

	/**
	 * Cleans a percentage input from a cell
	 *
	 * @receiver A string containing the value to clean
	 * @return An integer with the clean value
	 */
	private fun String.toPercentageInt(): Int {
		return this.removeSuffix("%").toDouble().toInt()
	}

	/**
	 * Checks if a String is null or blank (character '-')
	 *
	 * @return true if the string is null or blank false otherwise
	 */
	private fun String?.isNullOrBlank() = this == null || this == "-"
}