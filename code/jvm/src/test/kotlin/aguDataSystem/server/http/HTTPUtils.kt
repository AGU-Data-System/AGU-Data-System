package aguDataSystem.server.http


import aguDataSystem.server.http.models.agu.AGUCreateRequestModel
import aguDataSystem.server.http.models.contact.ContactCreationRequestModel
import aguDataSystem.server.http.models.dno.DNOCreationRequestModel
import aguDataSystem.server.http.models.gasLevels.GasLevelsRequestModel
import aguDataSystem.server.http.models.notes.NotesRequestModel
import aguDataSystem.server.http.models.tank.TankCreationRequestModel
import aguDataSystem.server.http.models.tank.TankUpdateRequestModel
import aguDataSystem.server.http.models.transportCompany.TransportCompanyRequestModel
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

	/**
	 * Util function:
	 *
	 * Sends a request to get an AGU by its ID
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @return the response body
	 */
	fun getAGURequest(client: WebTestClient, aguId: String) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to get all AGUs
	 * @param client the WebTestClient
	 * @return the response body
	 */
	fun getAllAGUsRequest(client: WebTestClient) =
		client.get()
			.uri("$BASE_AGU_PATH/")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to delete an AGU by its ID
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @return the response body
	 */
	fun deleteAGURequest(client: WebTestClient, aguId: String) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to get the temperature measures of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param days the number of days to get the measures from
	 */
	fun getTemperatureMeasuresRequest(client: WebTestClient, aguId: String, days: Int) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId/temperature?days=$days")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to get the daily gas measures of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param days the number of days to get the measures from
	 * @param time the time of the day to get the measures from
	 * @return the response body
	 */
	fun getDailyGasMeasuresRequest(client: WebTestClient, aguId: String, days: Int, time: LocalTime) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId/gas/daily?days=$days&time=$time")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to get the hourly gas measures of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param day the day to get the measures from
	 * @return the response body
	 */
	fun getHourlyGasMeasuresRequest(client: WebTestClient, aguId: String, day: LocalDate) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId/gas/hourly?day=$day")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to get the gas measures predictions of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param days the number of days to get the predictions from
	 * @param time the time of the day to get the predictions from
	 * @return the response body
	 */
	fun getPredictionGasMeasuresRequest(client: WebTestClient, aguId: String, days: Int, time: LocalTime) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId/gas/predictions?days=$days&time=$time")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to update the favourite state of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param isFavorite the new favourite state
	 */
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

	/**
	 * Util function:
	 *
	 * Sends a request to change the levels of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param gasLevelsModel the gas levels model
	 * @return the response body
	 */
	fun changeGasLevelsRequest(
		client: WebTestClient,
		aguId: String,
		gasLevelsModel: GasLevelsRequestModel
	) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/levels")
			.bodyValue(
				Json.encodeToJsonElement(gasLevelsModel)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to update the notes of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param notes the new notes
	 * @return the response body
	 */
	fun changeNotesRequest(client: WebTestClient, aguId: String, notes: NotesRequestModel) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/notes")
			.bodyValue(
				Json.encodeToJsonElement(notes)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// Contact
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

	fun deleteContactRequest(client: WebTestClient, aguId: String, contactId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId/contact/$contactId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// DNO
	fun getAllDNOsRequest(client: WebTestClient) =
		client.get()
			.uri("$BASE_AGU_PATH/dnos")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun createDNORequest(client: WebTestClient, dno: DNOCreationRequestModel) =
		client.post()
			.uri("$BASE_AGU_PATH/dnos")
			.bodyValue(
				Json.encodeToJsonElement(dno)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun deleteDNORequest(client: WebTestClient, dnoId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/dnos/$dnoId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// Transport Company
	fun getAllTransportCompaniesRequest(client: WebTestClient) =
		client.get()
			.uri("$BASE_AGU_PATH/transport-companies")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getTransportCompaniesOfAGURequest(client: WebTestClient, aguId: String) =
		client.get()
			.uri("$BASE_AGU_PATH/transport-companies/agu/$aguId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun addTransportCompanyRequest(client: WebTestClient, transportCompany: TransportCompanyRequestModel) =
		client.post()
			.uri("$BASE_AGU_PATH/transport-companies")
			.bodyValue(
				Json.encodeToJsonElement(transportCompany)
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun deleteTransportCompanyRequest(client: WebTestClient, transportCompanyId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/transport-companies/$transportCompanyId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun addTransportCompanyToAGURequest(client: WebTestClient, aguId: String, transportCompanyId: Int) =
		client.put()
			.uri("$BASE_AGU_PATH/transport-companies/agu/$aguId/$transportCompanyId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun removeTransportCompanyFromAGURequest(client: WebTestClient, aguId: String, transportCompanyId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/transport-companies/agu/$aguId/$transportCompanyId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// Tank
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

	fun deleteTankRequest(client: WebTestClient, aguId: String, tankId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// Clean
	fun cleanTest(
		client: WebTestClient,
		idAGU: String,
		idDNO: Int,
		idsTransportCompany: List<Int> = emptyList(),
		idsContact: List<Int> = emptyList(),
		idsTank: List<Int> = emptyList()
	) {
		idsContact.forEach { deleteContactRequest(client, idAGU, it) }

		idsTransportCompany.forEach { deleteTransportCompanyRequest(client, it) }

		idsTank.forEach { deleteTankRequest(client, idAGU, it) }

		// agu
		deleteAGURequest(client, idAGU)

		// dno
		deleteDNORequest(client, idDNO)
	}
}
