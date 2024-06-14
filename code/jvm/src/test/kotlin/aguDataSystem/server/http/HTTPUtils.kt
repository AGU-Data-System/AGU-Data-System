package aguDataSystem.server.http


import aguDataSystem.server.http.models.request.agu.AGUCreateRequestModel
import aguDataSystem.server.http.models.request.contact.ContactCreationRequestModel
import aguDataSystem.server.http.models.request.dno.DNOCreationRequestModel
import aguDataSystem.server.http.models.request.gasLevels.GasLevelsRequestModel
import aguDataSystem.server.http.models.request.notes.NotesRequestModel
import aguDataSystem.server.http.models.request.tank.TankCreationRequestModel
import aguDataSystem.server.http.models.request.tank.TankUpdateRequestModel
import aguDataSystem.server.http.models.request.transportCompany.TransportCompanyRequestModel
import aguDataSystem.server.http.models.response.agu.AGUCreationResponse
import aguDataSystem.server.http.models.response.agu.AGUResponse
import aguDataSystem.server.http.models.response.dno.DNOResponse
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
	private const val BASE_DNO_PATH = "/dnos"

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
			.responseBody!!

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
			.responseBody!!

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
			.responseBody!!

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
	/**
	 * Util function:
	 *
	 * Sends a request to add a contact to an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param contactModel the contact model
	 * @return the response body
	 */
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

	/**
	 * Util function:
	 *
	 * Sends a request to delete a contact from an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param contactId the contact ID
	 * @return the response body
	 */
	fun deleteContactRequest(client: WebTestClient, aguId: String, contactId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId/contact/$contactId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// DNO
	/**
	 * Util function:
	 *
	 * Sends a request to get all DNOs
	 *
	 * @param client the WebTestClient
	 * @return the response body
	 */
	fun getAllDNOsRequest(client: WebTestClient) =
		client.get()
			.uri(BASE_DNO_PATH)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to create a DNO
	 * @param client the WebTestClient
	 * @param dno the DNO creation model
	 * @return the response body
	 */
	fun createDNORequest(client: WebTestClient, dno: DNOCreationRequestModel) =
		client.post()
			.uri(BASE_DNO_PATH)
			.bodyValue(
				Json.encodeToJsonElement(dno)
			)
			.exchange()
			.expectStatus().isCreated
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to delete a DNO
	 * @param client the WebTestClient
	 * @param dnoId the DNO ID
	 * @return the response body
	 */
	fun deleteDNORequest(client: WebTestClient, dnoId: Int) =
		client.delete()
			.uri("$BASE_DNO_PATH/$dnoId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// Transport Company

	/**
	 * Util function:
	 *
	 * Sends a request to get all transport companies
	 * @param client the WebTestClient
	 * @return the response body
	 */
	fun getAllTransportCompaniesRequest(client: WebTestClient) =
		client.get()
			.uri("$BASE_AGU_PATH/transport-companies")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to get the transport companies of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @return the response body
	 */
	fun getTransportCompaniesOfAGURequest(client: WebTestClient, aguId: String) =
		client.get()
			.uri("$BASE_AGU_PATH/transport-companies/agu/$aguId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to add a transport company
	 * @param client the WebTestClient
	 * @param transportCompany the transport company model
	 * @return the response body
	 */
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

	/**
	 * Util function:
	 *
	 * Sends a request to delete a transport company
	 * @param client the WebTestClient
	 * @param transportCompanyId the transport company ID
	 * @return the response body
	 */
	fun deleteTransportCompanyRequest(client: WebTestClient, transportCompanyId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/transport-companies/$transportCompanyId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to add a transport company to an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param transportCompanyId the transport company ID
	 * @return the response body
	 */
	fun addTransportCompanyToAGURequest(client: WebTestClient, aguId: String, transportCompanyId: Int) =
		client.put()
			.uri("$BASE_AGU_PATH/transport-companies/agu/$aguId/$transportCompanyId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	/**
	 * Util function:
	 *
	 * Sends a request to remove a transport company from an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param transportCompanyId the transport company ID
	 * @return the response body
	 */
	fun removeTransportCompanyFromAGURequest(client: WebTestClient, aguId: String, transportCompanyId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/transport-companies/agu/$aguId/$transportCompanyId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// Tank
	/**
	 * Util function:
	 *
	 * Sends a request to add a tank to an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param tankModel the tank model
	 * @return the response body
	 */
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

	/**
	 * Util function:
	 *
	 * Sends a request to update a tank
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param tankId the tank ID
	 * @param tankUpdateModel the tank update model
	 * @return the response body
	 */
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

	/**
	 * Util function:
	 *
	 * Sends a request to delete a tank from an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param tankId the tank ID
	 * @return the response body
	 */
	fun deleteTankRequest(client: WebTestClient, aguId: String, tankId: Int) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	// Clean
	/**
	 * Util function:
	 *
	 * Cleans the test by deleting the AGU, DNO, contacts, transport companies and tanks
	 * @param client the WebTestClient
	 * @param idAGU the AGU ID
	 * @param idDNO the DNO ID
	 * @param idsTransportCompany the transport companies IDs
	 * @param idsContact the contact IDs'
	 * @param idsTank the tanks IDs
	 */
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

	// deserialize responses
	/**
	 * Util function:
	 *
	 * Deserializes the response body to a DNO response
	 * @receiver the response body
	 * @return the AGU response
	 */
	fun ByteArray.toDNOResponse() = Json.decodeFromString<DNOResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to an AGU response
	 * @receiver the response body
	 * @return the AGU response
	 */
	fun ByteArray.toAGUCreationResponse() = Json.decodeFromString<AGUCreationResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to an AGU response
	 * @receiver the response body
	 * @return the AGU response
	 */
	fun ByteArray.toAGUResponse() = Json.decodeFromString<AGUResponse>(this.decodeToString())
}
