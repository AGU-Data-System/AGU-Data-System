package aguDataSystem.server.http


import aguDataSystem.server.http.models.agu.AGUCreateRequestModel
import aguDataSystem.server.http.models.contact.ContactCreationRequestModel
import aguDataSystem.server.http.models.gasLevels.GasLevelsRequestModel
import aguDataSystem.server.http.models.notes.NotesRequestModel
import aguDataSystem.server.http.models.tank.TankCreationRequestModel
import aguDataSystem.server.http.models.tank.TankUpdateRequestModel
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
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
	 * @param aguCreationModel the AGUCreationInputModel
	 * @return the response body
	 */
	fun createAGURequest(client: WebTestClient, aguCreationModel: AGUCreateRequestModel) =
		client.post()
			.uri("$BASE_AGU_PATH/create")
			.bodyValue(
				Json.encodeToJsonElement(aguCreationModel)
					.also { println(it) }
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
				Json.encodeToJsonElement(isFavorite)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun addContactRequest(client: WebTestClient, aguId: String, contactModel: ContactCreationRequestModel) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/contact")
			.bodyValue(
				Json.encodeToJsonElement(contactModel)
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

	fun addTankRequest(client: WebTestClient, aguId: String, tankModel: TankCreationRequestModel) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank")
			.bodyValue(
				Json.encodeToJsonElement(tankModel)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun updateTankRequest(
		client: WebTestClient,
		aguId: String,
		tankId: String,
		tankUpdateModel: TankUpdateRequestModel
	) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId")
			.bodyValue(
				Json.encodeToJsonElement(tankUpdateModel)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun changeGasLevelsRequest(
		client: WebTestClient,
		aguId: String,
		tankId: String,
		gasLevelsModel: GasLevelsRequestModel
	) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId/gas")
			.bodyValue(
				Json.encodeToJsonElement(gasLevelsModel)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun changeNotesRequest(client: WebTestClient, aguId: String, tankId: String, notes: NotesRequestModel) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId/notes")
			.bodyValue(
				Json.encodeToJsonElement(notes)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody
}
