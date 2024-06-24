package aguDataSystem.server.http


import aguDataSystem.server.http.models.request.agu.AGUCreateRequestModel
import aguDataSystem.server.http.models.request.agu.UpdateActiveAGURequestModel
import aguDataSystem.server.http.models.request.contact.ContactCreationRequestModel
import aguDataSystem.server.http.models.request.dno.DNOCreationRequestModel
import aguDataSystem.server.http.models.request.gasLevels.GasLevelsRequestModel
import aguDataSystem.server.http.models.request.notes.NotesRequestModel
import aguDataSystem.server.http.models.request.tank.TankCreationRequestModel
import aguDataSystem.server.http.models.request.tank.TankUpdateRequestModel
import aguDataSystem.server.http.models.request.transportCompany.TransportCompanyRequestModel
import aguDataSystem.server.http.models.response.agu.AGUBasicInfoListResponse
import aguDataSystem.server.http.models.response.agu.AGUCreationResponse
import aguDataSystem.server.http.models.response.agu.AGUResponse
import aguDataSystem.server.http.models.response.contact.ContactCreationResponse
import aguDataSystem.server.http.models.response.contact.ContactResponse
import aguDataSystem.server.http.models.response.dno.DNOListResponse
import aguDataSystem.server.http.models.response.dno.DNOResponse
import aguDataSystem.server.http.models.response.tank.TankCreationResponse
import aguDataSystem.server.http.models.response.tank.TankResponse
import aguDataSystem.server.http.models.response.transportCompany.TransportCompanyCreationResponse
import aguDataSystem.server.http.models.response.transportCompany.TransportCompanyListResponse
import aguDataSystem.server.http.models.response.transportCompany.TransportCompanyResponse
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Requests
 */
object HTTPUtils {

	private const val BASE_AGU_PATH = "/agus"
	private const val BASE_DNO_PATH = "/dnos"
	private const val BASE_TRANSPORT_COMPANY_PATH = "/transport-companies"

	/**
	 * Util function:
	 *
	 * Sends a request to create an AGU with its creation input model
	 * @param client the WebTestClient
	 * @param aguCreationModel the AGUCreationInputModel
	 * @return the response body
	 */
	fun createAGURequest(client: WebTestClient, aguCreationModel: AGUCreateRequestModel) =
		createAGURequestWithStatusCode(client, aguCreationModel, HttpStatus.CREATED)

	/**
	 * Util function:
	 *
	 * Sends a request to create an AGU with its creation input model and expects an error
	 * @param client the WebTestClient
	 * @param aguCreationModel the AGUCreationInputModel
	 * @param status the expected status
	 * @return the response body
	 */
	fun createAGURequestWithStatusCode(
		client: WebTestClient,
		aguCreationModel: AGUCreateRequestModel,
		status: HttpStatusCode
	) =
		client.post()
			.uri("$BASE_AGU_PATH/create")
			.bodyValue(
				Json.encodeToJsonElement(aguCreationModel)
			)
			.exchange()
			.expectStatus().isEqualTo(status)
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
		getAGURequestWithStatusCode(client, aguId, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to get an AGU by its ID and expects a status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun getAGURequestWithStatusCode(client: WebTestClient, aguId: String, status: HttpStatusCode) =
		client.get()
			.uri("$BASE_AGU_PATH/$aguId")
			.exchange()
			.expectStatus().isEqualTo(status)
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
			.uri(BASE_AGU_PATH)
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
		deleteAGURequestWithStatusCode(client, aguId, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to delete an AGU by its ID and expects a status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun deleteAGURequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		status: HttpStatusCode
	) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId")
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

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
	 * @param isFavourite the new favourite state
	 */
	fun updateFavouriteStateRequest(client: WebTestClient, aguId: String, isFavourite: Boolean) =
		updateFavouriteStateRequestWithStatusCode(client, aguId, isFavourite, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to update the favourite state of an AGU and expects a status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param isFavourite the new favourite state
	 * @param status the expected status
	 */
	fun updateFavouriteStateRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		isFavourite: Boolean,
		status: HttpStatusCode
	) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/favourite") // for some reason, if aguId is empty, the uri is http://localhost:8080/api/agus/favorite
			.bodyValue(
				Json.encodeToJsonElement(isFavourite)
			)
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to update the active state of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param isActive the new active state
	 * @return the response body
	 */
	fun updateActiveStateRequest(client: WebTestClient, aguId: String, isActive: Boolean) =
		updateActiveStateRequestWithStatusCode(client, aguId, isActive, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to update the active state of an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param isActive the new active state
	 * @param status the expected status
	 * @return the response body
	 */
	fun updateActiveStateRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		isActive: Boolean,
		status: HttpStatusCode
	) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/active") // for some reason, if aguId is empty, the uri is http://localhost:8080/api/agus/active
			.bodyValue(
				Json.encodeToJsonElement(UpdateActiveAGURequestModel(isActive))
			)
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to change the gas levels of an AGU
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
		changeGasLevelsRequestWithStatusCode(client, aguId, gasLevelsModel, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to change the levels of an AGU and expects a status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param gasLevelsModel the gas levels model
	 * @param status the expected status
	 * @return the response body
	 */
	fun changeGasLevelsRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		gasLevelsModel: GasLevelsRequestModel,
		status: HttpStatusCode
	) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/levels") // for some reason, if aguId is empty, the uri is http://localhost:8080/api/agus/levels
			.bodyValue(
				Json.encodeToJsonElement(gasLevelsModel)
			)
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to change the notes of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param notes the new notes
	 * @return the response body
	 */
	fun changeNotesRequest(client: WebTestClient, aguId: String, notes: NotesRequestModel) =
		changeNotesRequestWithStatusCode(client, aguId, notes, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to update the notes of an AGU and expects a status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param notesModel the new notes model
	 * @param status the expected status
	 * @return the response body
	 */
	fun changeNotesRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		notesModel: NotesRequestModel,
		status: HttpStatusCode
	) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/notes")
			.bodyValue(
				Json.encodeToJsonElement(notesModel)
			)
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

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
		addContactRequestWithStatusCode(client, aguId, contactModel, HttpStatus.CREATED)

	/**
	 * Util function:
	 *
	 * Sends a request to add a contact to an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param contact the contact model
	 * @param status the expected status
	 * @return the response body
	 */
	fun addContactRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		contact: ContactCreationRequestModel,
		status: HttpStatus
	) =
		client.post()
			.uri("$BASE_AGU_PATH/$aguId/contact")
			.bodyValue(Json.encodeToJsonElement(contact))
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

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
		deleteContactRequestWithStatusCode(client, aguId, contactId, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to delete a contact from an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param contactId the contact ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun deleteContactRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		contactId: Int,
		status: HttpStatusCode
	) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId/contact/$contactId")
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

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
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to create a DNO
	 * @param client the WebTestClient
	 * @param dno the DNO creation model
	 * @return the response body
	 */
	fun createDNORequest(client: WebTestClient, dno: DNOCreationRequestModel) =
		createDNORequestWithStatusCode(client, dno, HttpStatus.CREATED)

	/**
	 * Util function:
	 *
	 * Sends a request to create a DNO and expects a specific status code
	 * @param client the WebTestClient
	 * @param dno the DNO creation model
	 * @param status the expected status
	 * @return the response body
	 */
	fun createDNORequestWithStatusCode(client: WebTestClient, dno: DNOCreationRequestModel, status: HttpStatusCode) =
		client.post()
			.uri(BASE_DNO_PATH)
			.bodyValue(
				Json.encodeToJsonElement(dno)
			)
			.exchange()
			.expectStatus().isEqualTo(status)
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
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to delete a DNO and expects a specific status code
	 * @param client the WebTestClient
	 * @param dnoId the DNO ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun deleteDNORequestWithStatusCode(client: WebTestClient, dnoId: Int, status: HttpStatusCode) =
		client.delete()
			.uri("$BASE_DNO_PATH/$dnoId")
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

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
			.uri(BASE_TRANSPORT_COMPANY_PATH)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to get the transport companies of an AGU
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @return the response body
	 */
	fun getTransportCompaniesOfAGURequest(client: WebTestClient, aguId: String) =
		getTransportCompaniesOfAGURequestWithStatusCode(client, aguId, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to get the transport companies of an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun getTransportCompaniesOfAGURequestWithStatusCode(client: WebTestClient, aguId: String, status: HttpStatusCode) =
		client.get()
			.uri("$BASE_TRANSPORT_COMPANY_PATH/agu/$aguId")
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to add a transport company
	 * @param client the WebTestClient
	 * @param transportCompany the transport company model
	 * @return the response body
	 */
	fun addTransportCompanyRequest(client: WebTestClient, transportCompany: TransportCompanyRequestModel) =
		addTransportCompanyRequestWithStatusCode(client, transportCompany, HttpStatus.CREATED)

	/**
	 * Util function:
	 *
	 * Sends a request to add a transport company and expects a specific status code
	 * @param client the WebTestClient
	 * @param transportCompany the transport company model
	 * @param status the expected status
	 * @return the response body
	 */
	fun addTransportCompanyRequestWithStatusCode(
		client: WebTestClient,
		transportCompany: TransportCompanyRequestModel,
		status: HttpStatusCode
	) =
		client.post()
			.uri(BASE_TRANSPORT_COMPANY_PATH)
			.bodyValue(
				Json.encodeToJsonElement(transportCompany)
			)
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

	/**
	 * Util function:
	 *
	 * Sends a request to delete a transport company
	 * @param client the WebTestClient
	 * @param transportCompanyId the transport company ID
	 * @return the response body
	 */
	fun deleteTransportCompanyRequest(client: WebTestClient, transportCompanyId: Int) =
		deleteTransportCompanyRequestWithStatusCode(client, transportCompanyId, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to delete a transport company and expects a specific status code
	 * @param client the WebTestClient
	 * @param transportCompanyId the transport company ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun deleteTransportCompanyRequestWithStatusCode(
		client: WebTestClient,
		transportCompanyId: Int,
		status: HttpStatusCode
	) =
		client.delete()
			.uri("$BASE_TRANSPORT_COMPANY_PATH/$transportCompanyId")
			.exchange()
			.expectStatus().isEqualTo(status)
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
		addTransportCompanyToAGURequestWithStatusCode(client, aguId, transportCompanyId, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to add a transport company to an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param transportCompanyId the transport company ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun addTransportCompanyToAGURequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		transportCompanyId: Int,
		status: HttpStatusCode
	) =
		client.put()
			.uri("$BASE_TRANSPORT_COMPANY_PATH/$transportCompanyId/agu/$aguId")
			.exchange()
			.expectStatus().isEqualTo(status)
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
		removeTransportCompanyFromAGURequestWithStatusCode(client, aguId, transportCompanyId, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to remove a transport company from an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param transportCompanyId the transport company ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun removeTransportCompanyFromAGURequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		transportCompanyId: Int,
		status: HttpStatusCode
	) =
		client.delete()
			.uri("$BASE_TRANSPORT_COMPANY_PATH/$transportCompanyId/agu/$aguId")
			.exchange()
			.expectStatus().isEqualTo(status)
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
		addTankRequestWithStatusCode(client, aguId, tankModel, HttpStatus.CREATED)

	/**
	 * Util function:
	 *
	 * Sends a request to add a tank to an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param tankModel the tank model
	 * @param status the expected status
	 */
	fun addTankRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		tankModel: TankCreationRequestModel,
		status: HttpStatusCode
	) =
		client.post()
			.uri("$BASE_AGU_PATH/$aguId/tank")
			.bodyValue(
				Json.encodeToJsonElement(tankModel)
			)
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

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
	) = updateTankRequestWithStatusCode(client, aguId, tankId, tankUpdateModel, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to update a tank in an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param tankId the tank ID
	 * @param tankUpdateModel the tank update model
	 * @param status the expected status
	 * @return the response body
	 */
	fun updateTankRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		tankId: String,
		tankUpdateModel: TankUpdateRequestModel,
		status: HttpStatusCode
	) =
		client.put()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId")
			.bodyValue(
				Json.encodeToJsonElement(tankUpdateModel)
			)
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

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
		deleteTankRequestWithStatusCode(client, aguId, tankId, HttpStatus.OK)

	/**
	 * Util function:
	 *
	 * Sends a request to delete a tank from an AGU and expects a specific status code
	 * @param client the WebTestClient
	 * @param aguId the AGU ID
	 * @param tankId the tank ID
	 * @param status the expected status
	 * @return the response body
	 */
	fun deleteTankRequestWithStatusCode(
		client: WebTestClient,
		aguId: String,
		tankId: Int,
		status: HttpStatusCode
	) =
		client.delete()
			.uri("$BASE_AGU_PATH/$aguId/tank/$tankId")
			.exchange()
			.expectStatus().isEqualTo(status)
			.expectBody()
			.returnResult()
			.responseBody!!

	// Clean
	/**
	 * Util function:
	 *
	 * Cleans the test by deleting the AGU, DNO, contacts, transport companies and tanks
	 * @param client the WebTestClient
	 * @param idAGU the AGU ID
	 * @param idDNO the DNO ID
	 * @param idsTransportCompany the transport companies IDs
	 */
	fun cleanTest(
		client: WebTestClient,
		idAGU: String? = null,
		idDNO: Int? = null,
		idsTransportCompany: List<Int> = emptyList()
	) {
		if (idAGU == null) {
			idsTransportCompany.forEach { deleteTransportCompanyRequest(client, it) }
		}

		if (idAGU != null) {
			deleteAGURequest(client, idAGU)
		}

		if (idDNO != null) {
			deleteDNORequest(client, idDNO)
		}
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

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a list of AGU responses
	 * @receiver the response body
	 * @return the list of AGU responses
	 */
	fun ByteArray.toAllAGUResponse() = Json.decodeFromString<AGUBasicInfoListResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a list of DNO responses
	 * @receiver the response body
	 * @return the list of DNO responses
	 */
	fun ByteArray.toAllDNOResponse() = Json.decodeFromString<DNOListResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a contact response
	 * @receiver the response body
	 * @return the contact response
	 */
	fun ByteArray.toContactResponse() = Json.decodeFromString<ContactResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a contact creation response
	 * @receiver the response body
	 * @return the contact creation response
	 */
	fun ByteArray.toContactCreationResponse() = Json.decodeFromString<ContactCreationResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a Tank response
	 * @receiver the response body
	 * @return the Tank response
	 */
	fun ByteArray.toTankResponse() = Json.decodeFromString<TankResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a TankCreationResponse
	 * @receiver the response body
	 * @return the TankCreationResponse
	 */
	fun ByteArray.toTankCreationResponse() = Json.decodeFromString<TankCreationResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a TransportCompanyListResponse
	 * @receiver the response body
	 * @return the TransportCompanyListResponse
	 */
	fun ByteArray.toTransportCompanyListResponse() =
		Json.decodeFromString<TransportCompanyListResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a TransportCompanyResponse
	 * @receiver the response body
	 * @return the TransportCompanyResponse
	 */
	fun ByteArray.toTransportCompanyResponse() =
		Json.decodeFromString<TransportCompanyResponse>(this.decodeToString())

	/**
	 * Util function:
	 *
	 * Deserializes the response body to a TransportCompanyCreationResponse
	 * @receiver the response body
	 * @return the TransportCompanyCreationResponse
	 */
	fun ByteArray.toTransportCompanyCreationResponse() =
		Json.decodeFromString<TransportCompanyCreationResponse>(this.decodeToString())

}
