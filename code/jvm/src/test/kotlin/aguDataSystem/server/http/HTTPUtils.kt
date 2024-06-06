package aguDataSystem.server.http

import aguDataSystem.server.http.controllers.agu.models.input.agu.AGUCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.contact.ContactCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.gasLevels.GasLevelsInputModel
import aguDataSystem.server.http.controllers.agu.models.input.tank.TankCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.tank.TankUpdateInputModel
import java.time.LocalDate
import java.time.LocalTime
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Requests
 */
object HTTPUtils {

	private const val BASE_AGU_PATH = "/agus"

	/**
	 * Util function:
	 *
	 * Sends a request to create an AGU with its creation input model
	 * @param client the WebTestClient
	 * @param aguCreation the AGUCreationInputModel
	 * @return the response body
	 */
	fun createAGURequest(client: WebTestClient, aguCreation: AGUCreationInputModel) =
		client.post()
			.bodyValue(
				aguCreation.toMap()
					.also { println(it.toMapString()) }
			)
			.exchange()
			.expectStatus().isCreated
			.expectBody()
			.returnResult()
			.responseBody


	fun getAGURequest(client: WebTestClient, aguId: String) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getAllAGUsRequest(client: WebTestClient) =
		client.get()
			.uri("$BASE_AGU_PATH/")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getTemperatureMeasuresRequest(client: WebTestClient, aguId: String, days: Int) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId/temperature?days=$days")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getDailyGasMeasuresRequest(client: WebTestClient, aguId: String, days: Int, time: LocalTime) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId/gas/daily?days=$days&time=$time")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getHourlyGasMeasuresRequest(client: WebTestClient, aguId: String, day: LocalDate) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId/gas/hourly?day=$day")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getPredictionGasMeasuresRequest(client: WebTestClient, aguId: String, days: Int, time: LocalTime) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId/gas/predictions?days=$days&time=$time")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getFavoriteAGUsRequest(client: WebTestClient) =
		client.get()
			.uri("$BASE_AGU_PATH/favorites")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun updateFavouriteStateRequest(client: WebTestClient, aguId: String, isFavorite: Boolean) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/favorite")
			.bodyValue(
				"isFavorite" to isFavorite
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun addContactRequest(client: WebTestClient, aguId: String, contactInputModel: ContactCreationInputModel) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/contact")
			.bodyValue(
				"contact" to contactInputModel
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun deleteContactRequest(client: WebTestClient, aguId: String, contactId: String) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId/contact/$contactId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun addTankRequest(client: WebTestClient, aguId: String, tankInputModel: TankCreationInputModel) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank")
			.bodyValue(
				"tank" to tankInputModel
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun updateTankRequest(client: WebTestClient, aguId: String, tankId: String, tankUpdateInputModel: TankUpdateInputModel) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId")
			.bodyValue(
				"tank" to tankUpdateInputModel
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun changeGasLevelsRequest(client: WebTestClient, aguId: String, tankId: String, gasLevelsInputModel: GasLevelsInputModel) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId/gas")
			.bodyValue(
				"gasLevels" to gasLevelsInputModel
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun changeNotesRequest(client: WebTestClient, aguId: String, tankId: String, notes: String) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId/notes")
			.bodyValue(
				"notes" to notes
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

}

/**
 * Converts an object to a map of its properties
 * TODO fix this
 * @receiver the object
 * @return the map of the object's properties
 */
private fun Any?.toMap(): Map<String, Any?> {
	if (this == null) return emptyMap()
	val clazz = this::class

	// Skip Java standard library classes
	if (clazz.qualifiedName?.startsWith("java.") == true) {
		return mapOf("value" to this.toString())
	}

	return clazz.memberProperties.associate { prop ->
		val kProperty1 = prop as KProperty1<Any, *>
		val value = try {
			kProperty1.isAccessible = true
			kProperty1.get(this)
		} catch (e: Exception) {
			null // Inaccessible properties are set to null
		}

		val mapValue = when (value) {
			is Iterable<*> -> value.map { it?.toMap() }
			is Array<*> -> value.map { it?.toMap() }
			is Map<*, *> -> value.entries.associate { it.key.toString() to it.value?.toMap() }
			is ByteArray -> value.toList() // convert ByteArray to List<Byte>
			else -> if (value != null && value::class.isData) value.toMap() else value
		}
		prop.name to mapValue
	}
}

/**
 * Converts an object to a string representation of its map
 */
private fun Any?.toMapString(): String {
	return this?.toMap().toString()
}