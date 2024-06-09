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
    val gasLevelUrl: String? = null,
    val image: ByteArray? = null,
    val tanks: List<TankCreationInputModel> = emptyList(),
    val contacts: List<ContactCreationInputModel> = emptyList(),
    val transportCompanies: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isActive: Boolean = true,
    val notes: String? = null,
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
        aguProcessor.processCSV("code/jvm/src/main/kotlin/script/UAGS GL UAG ISEL 27.05.xlsx") // TODO: Change to actual path
    }
}

class AGUProcessor {
    private val apiUrl = "http://localhost:8080/api/agus/create"
    private val client = HttpClient.newHttpClient()
    private val jsonFormatter = Json { prettyPrint = true }
    private val logger = LoggerFactory.getLogger(AGUProcessor::class.java)

    fun processCSV(csvPath: String) {
        val rows = csvReader {
            charset = "UTF-8"
            this.insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
            autoRenameDuplicateHeaders = true
        }.readAllWithHeader(File(csvPath))

        rows.forEach { row ->
            try {
                val transportCompanies = row.filter { it.key.startsWith("Transportador (") && it.value == "true" }
                    .map { it.key.removePrefix("Transportador (").removeSuffix(")") }

                val tanks = (1..3).mapNotNull { i ->
                    if (row["Volume Tk$i (m3)"].isNullOrBlank()) null
                    else TankCreationInputModel(
                        number = i,
                        minLevel = row["UAG Min Lvl Tk$i"]!!.toInt(),
                        criticalLevel = row["UAG Crit Lvl Tk$i"]!!.toInt(),
                        maxLevel = row["UAG MaxLvl Tk$i"]!!.toInt(),
                        correctionFactor = row["UAG Fator de Correção Tk$i"]!!.toDouble(),
                        capacity = row["Volume Tk$i (m3)"]!!.toDouble()
                    )
                }

                val contacts = mutableListOf<ContactCreationInputModel>()
                contacts.add(ContactCreationInputModel(
                    name = row["Nome Contacto Logistica"]!!,
                    phone = row["Nº Contacto Logistica"]!!,
                    type = "logistic"
                ))
                contacts.add(ContactCreationInputModel(
                    name = row["Nome Contacto Emergencia"]!!,
                    phone = row["Nº Contacto Emergencia"]!!,
                    type = "emergency"
                ))
                contacts.add(ContactCreationInputModel(
                    name = row["Nome substituto"]!!,
                    phone = row["Nº Contacto Emergencia substituto"]!!,
                    type = "emergency"
                ))

                val aguInput = AddAGUInputModel(
                    cui = row["CUI"]!!,
                    eic = row["EIC"]!!,
                    name = row["Nome UAG na REN"]!!,
                    minLevel = row["UAG Min Lvl"]!!.removeSuffix("%").toInt(),
                    maxLevel = row["UAG Max Lvl"]!!.removeSuffix("%").toInt(),
                    criticalLevel = row["UAG Crit Lvl min"]!!.removeSuffix("%").toInt(),
                    loadVolume = row["% carga no Vol Total (cist. 20 ton)"]!!.toDouble(),
                    latitude = row["Latitude"]!!.toDouble(),
                    longitude = row["Longitude"]!!.toDouble(),
                    locationName = row["Localidade"]!!,
                    dnoName = row["Nome ORD"]!!,
                    transportCompanies = transportCompanies,
                    notes = row["Observações"],
                    correctionFactor = row["UAG Fator de Correção"]!!.toDouble(),
                    tanks = tanks,
                    contacts = contacts,
                    isActive = row["Activo/ Inactivo"] == "Activo",
                )
                sendPostRequest(aguInput)
            } catch (e: Exception) {
                logger.error("Error processing row: $row", e)
            }
        }
    }

    private fun sendPostRequest(aguInput: AddAGUInputModel) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(jsonFormatter.encodeToString(aguInput)))
            .build()

        try {
            val response = client.send(request, BodyHandlers.ofString())
            logger.info("Response status code: ${response.statusCode()}")
            logger.info("Response body: ${response.body()}")
        } catch (e: Exception) {
            logger.error("Error sending POST request: ${e.message}")
        }
    }
}
