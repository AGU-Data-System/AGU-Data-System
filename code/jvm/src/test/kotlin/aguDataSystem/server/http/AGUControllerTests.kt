package aguDataSystem.server.http

import aguDataSystem.server.http.ControllerUtils.dummyGasLevelsRequestModel
import aguDataSystem.server.http.ControllerUtils.newTestAGU
import aguDataSystem.server.http.ControllerUtils.newTestDNO
import aguDataSystem.server.http.HTTPUtils.changeGasLevelsRequest
import aguDataSystem.server.http.HTTPUtils.changeGasLevelsRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.changeNotesRequest
import aguDataSystem.server.http.HTTPUtils.changeNotesRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.cleanTest
import aguDataSystem.server.http.HTTPUtils.createAGURequest
import aguDataSystem.server.http.HTTPUtils.createAGURequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.createDNORequest
import aguDataSystem.server.http.HTTPUtils.deleteAGURequest
import aguDataSystem.server.http.HTTPUtils.deleteAGURequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.getAGURequest
import aguDataSystem.server.http.HTTPUtils.getAGURequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.getAllAGUsRequest
import aguDataSystem.server.http.HTTPUtils.toAGUCreationResponse
import aguDataSystem.server.http.HTTPUtils.toAGUResponse
import aguDataSystem.server.http.HTTPUtils.toAllAGUResponse
import aguDataSystem.server.http.HTTPUtils.toDNOResponse
import aguDataSystem.server.http.HTTPUtils.updateActiveStateRequest
import aguDataSystem.server.http.HTTPUtils.updateActiveStateRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.updateFavouriteStateRequest
import aguDataSystem.server.http.HTTPUtils.updateFavouriteStateRequestWithStatusCode
import aguDataSystem.server.http.models.request.gasLevels.GasLevelsRequestModel
import aguDataSystem.server.http.models.request.notes.NotesRequestModel
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AGUControllerTests {

	// One of the very few places where we use property injection
	@LocalServerPort
	private val port: Int = 8080

	private val baseURL = "http://localhost:$port/api"

	private val testTimeOut = Duration.ofHours(1)

	@Test
	fun `create AGU properly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		createDNORequest(client, dno)

		// act and assert
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = allAgu.dno.id,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `create AGU twice should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		createDNORequest(client, dno)

		// act
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = allAgu.dno.id,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `create AGU with invalid cui should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name).copy(cui = "invalid")
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with empty cui should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name).copy(cui = "")
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with already used cui should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = newTestAGU(dnoName = dno.name).copy(cui = aguCreation.cui)
		createDNORequest(client, dno)
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = allAgu.dno.id,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `create AGU with empty eic should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name).copy(eic = "")
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with already used EIC should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = newTestAGU(dnoName = dno.name).copy(eic = aguCreation.eic)
		createDNORequest(client, dno)
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = allAgu.dno.id,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `create AGU with invalid name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name).copy(name = "")
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with the same name twice should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = newTestAGU(dnoName = dno.name).copy(name = aguCreation.name)
		createDNORequest(client, dno)
		createAGURequest(client, aguCreation)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, aguCreation.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = allAgu.dno.id,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `create AGU with invalid min level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name).copy(minLevel = -1)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation = newTestAGU(dnoName = dno.name).copy(maxLevel = -1)

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation = newTestAGU(dnoName = dno.name).copy(criticalLevel = -1)

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with critical over min level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation =
			newTestAGU(dnoName = dno.name).copy(minLevel = newTestAGU(dnoName = dno.name).criticalLevel - 1)

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with critical over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation =
			newTestAGU(dnoName = dno.name).copy(criticalLevel = newTestAGU(dnoName = dno.name).maxLevel + 1)

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with min level over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation = newTestAGU(dnoName = dno.name).copy(minLevel = newTestAGU(dnoName = dno.name).maxLevel + 1)

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid latitude should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation = newTestAGU(dnoName = dno.name).copy(latitude = -91.0)

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid longitude should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation = newTestAGU(dnoName = dno.name).copy(longitude = -181.0)

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with empty DNO name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation = newTestAGU(dnoName = dno.name).copy(dnoName = "")

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.NOT_FOUND)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with un existing DNO should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = newTestAGU(dnoName = newTestDNO().name)

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.NOT_FOUND)

		// clean
		// no clean needed
	}

	@Test
	fun `create AGU with invalid gas URL should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val aguCreation = newTestAGU(dnoName = dno.name).copy(gasLevelUrl = "invalid")

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid contact type should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation =
			newTestAGU(dnoName = dno.name).copy(contacts = newTestAGU(dnoName = dno.name).contacts.map { it.copy(type = "invalid") })
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid contact phone should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation =
			newTestAGU(dnoName = dno.name).copy(contacts = newTestAGU(dnoName = dno.name).contacts.map { it.copy(phone = "invalid") })
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid contact name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation =
			newTestAGU(dnoName = dno.name).copy(contacts = newTestAGU(dnoName = dno.name).contacts.map { it.copy(name = "") })
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, aguCreation, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU without any tank should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(tanks = emptyList())
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank number should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(number = -1)))
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank capacity should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(capacity = -1)))
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank min level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(minLevel = -1)))
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(maxLevel = -1)))
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(criticalLevel = -1)))
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with tank critical level over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(
			tanks = listOf(aguCreation.tanks.first().copy(criticalLevel = aguCreation.tanks.first().maxLevel + 1))
		)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with tank min level over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(
			tanks = listOf(aguCreation.tanks.first().copy(minLevel = aguCreation.tanks.first().maxLevel + 1))
		)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with tank min level under critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(
			tanks = listOf(aguCreation.tanks.first().copy(minLevel = aguCreation.tanks.first().criticalLevel - 1))
		)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with tank max level under critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(
			tanks = listOf(aguCreation.tanks.first().copy(maxLevel = aguCreation.tanks.first().criticalLevel - 1))
		)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with un existing transport companies should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(transportCompanies = listOf("un existing"))
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.NOT_FOUND)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with empty transport company name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val sut = aguCreation.copy(transportCompanies = listOf(""))
		val dnoId = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.NOT_FOUND)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `update AGU favorite state correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act
		val updatedAGU = updateFavouriteStateRequest(client, createdAGU.cui, !aguCreation.isFavourite).toAGUResponse()

		// assert
		assertNotEquals(updatedAGU.isFavourite, aguCreation.isFavourite)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU favorite state with invalid CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		updateFavouriteStateRequestWithStatusCode(client, "invalid", !aguCreation.isFavourite, HttpStatus.NOT_FOUND)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()

		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoID,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU favorite state with empty CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		updateFavouriteStateRequestWithStatusCode(client, "", !aguCreation.isFavourite, HttpStatus.METHOD_NOT_ALLOWED)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()

		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoID,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU favorite state with un-existing AGU should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		updateFavouriteStateRequestWithStatusCode(
			client,
			"PT6543210987654321XX",
			!aguCreation.isFavourite,
			HttpStatus.NOT_FOUND
		)

		// clean
		cleanTest(client = client, idDNO = dnoID)
	}

	@Test
	fun `update AGU active state correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()

		// act
		val updatedAGU = updateActiveStateRequest(client, createdAGU.cui, !systemAGU.isActive).toAGUResponse()

		// assert
		assertNotEquals(updatedAGU.isActive, systemAGU.isActive)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU active state with invalid CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		updateActiveStateRequestWithStatusCode(client, "invalid", !aguCreation.isActive, HttpStatus.NOT_FOUND)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()

		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoID,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU active state with empty CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		updateActiveStateRequestWithStatusCode(client, "", !aguCreation.isActive, HttpStatus.METHOD_NOT_ALLOWED)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()

		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoID,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU active state with un-existing AGU should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		updateActiveStateRequestWithStatusCode(
			client,
			"PT6543210987654321XX",
			!aguCreation.isActive,
			HttpStatus.NOT_FOUND
		)

		// clean
		cleanTest(client = client, idDNO = dnoID)
	}

	@Test
	fun `change AGU gas levels correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newGasLevels = dummyGasLevelsRequestModel

		// act
		val updatedAGU = changeGasLevelsRequest(client, createdAGU.cui, newGasLevels).toAGUResponse()

		// assert
		assertEquals(updatedAGU.levels.min, newGasLevels.min)
		assertEquals(updatedAGU.levels.max, newGasLevels.max)
		assertEquals(updatedAGU.levels.critical, newGasLevels.critical)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `change AGU gas levels with invalid CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val newGasLevels = GasLevelsRequestModel(min = 10, max = 20, critical = 15)

		// act and assert
		changeGasLevelsRequestWithStatusCode(client, "invalid", newGasLevels, HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `change AGU gas levels with empty CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val newGasLevels = GasLevelsRequestModel(min = 10, max = 20, critical = 15)

		// act and assert
		changeGasLevelsRequestWithStatusCode(client, "", newGasLevels, HttpStatus.METHOD_NOT_ALLOWED)
	}

	@Test
	fun `change AGU gas levels with un-existing AGU should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val newGasLevels = GasLevelsRequestModel(min = 10, max = 20, critical = 15)

		// act and assert
		changeGasLevelsRequestWithStatusCode(
			client,
			"PT6543210987654321XX",
			newGasLevels,
			HttpStatus.BAD_REQUEST
		)
	}

	@Test
	fun `change AGU gas levels with invalid min level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newGasLevels = GasLevelsRequestModel(min = -1, max = 20, critical = 15)

		// act and assert
		changeGasLevelsRequestWithStatusCode(client, createdAGU.cui, newGasLevels, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `change AGU gas levels with invalid max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newGasLevels = GasLevelsRequestModel(min = 10, max = -1, critical = 15)

		// act and assert
		changeGasLevelsRequestWithStatusCode(client, createdAGU.cui, newGasLevels, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `change AGU gas levels with invalid critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newGasLevels = GasLevelsRequestModel(min = 10, max = 20, critical = -1)

		// act and assert
		changeGasLevelsRequestWithStatusCode(client, createdAGU.cui, newGasLevels, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `change AGU gas levels with critical over min level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newGasLevels = dummyGasLevelsRequestModel.copy(critical = dummyGasLevelsRequestModel.min + 1)

		// act and assert
		changeGasLevelsRequestWithStatusCode(client, createdAGU.cui, newGasLevels, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `change AGU gas levels with critical over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newGasLevels = GasLevelsRequestModel(min = 5, max = 15, critical = 20)

		// act and assert
		changeGasLevelsRequestWithStatusCode(client, createdAGU.cui, newGasLevels, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `change AGU gas levels with min level over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newGasLevels = GasLevelsRequestModel(min = 20, max = 10, critical = 15)

		// act and assert
		changeGasLevelsRequestWithStatusCode(client, createdAGU.cui, newGasLevels, HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU notes correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		val newNotes = NotesRequestModel("Updated notes")

		// act
		val updatedAGU = changeNotesRequest(client, createdAGU.cui, newNotes).toAGUResponse()

		// assert
		assertNotEquals(updatedAGU.notes, systemAGU.notes)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoId,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU notes with invalid CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newNotes = NotesRequestModel("Updated notes")

		// act and assert
		changeNotesRequestWithStatusCode(client, "invalid", newNotes, HttpStatus.NOT_FOUND)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()

		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoID,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU notes with empty CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val newNotes = NotesRequestModel("Updated notes")

		// act and assert
		changeNotesRequestWithStatusCode(client, "", newNotes, HttpStatus.METHOD_NOT_ALLOWED)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()

		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoID,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `update AGU notes with un-existing AGU should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val newNotes = NotesRequestModel("Updated notes")

		// act and assert
		changeNotesRequestWithStatusCode(
			client,
			aguCreation.cui,
			newNotes,
			HttpStatus.NOT_FOUND
		)

		// clean
		cleanTest(client = client, idDNO = dnoID)
	}

	@Test
	fun `delete AGU correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()

		// act
		deleteAGURequest(client, createdAGU.cui)

		// assert
		val allAgu = getAllAGUsRequest(client).toAllAGUResponse()
		assertFalse(allAgu.agusBasicInfo.map { it.cui }.contains(systemAGU.cui))

		// clean
		cleanTest(
			client = client,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `delete AGU with invalid CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		deleteAGURequestWithStatusCode(client, "invalid", HttpStatus.BAD_REQUEST)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoID,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `delete AGU with empty CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		deleteAGURequestWithStatusCode(client, "", HttpStatus.NOT_FOUND)

		// clean
		val allAgu = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = allAgu.cui,
			idDNO = dnoID,
			idsTransportCompany = allAgu.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `delete AGU with un-existing AGU should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoID = createDNORequest(client, dno).toDNOResponse().id

		// act and assert
		deleteAGURequestWithStatusCode(client, aguCreation.cui, HttpStatus.OK)

		// clean
		cleanTest(client = client, idDNO = dnoID)
	}

	@Test
	fun `get all AGUs correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU1 = createAGURequest(client, newTestAGU(dnoName = dno.name)).toAGUCreationResponse()
		val createdAGU2 = createAGURequest(client, newTestAGU(dnoName = dno.name)).toAGUCreationResponse()

		// act
		val allAgu = getAllAGUsRequest(client).toAllAGUResponse()

		// assert
		assertTrue(allAgu.agusBasicInfo.any { it.cui == createdAGU1.cui })
		assertTrue(allAgu.agusBasicInfo.any { it.cui == createdAGU2.cui })

		// clean
		cleanTest(
			client = client,
			idAGU = createdAGU1.cui,
			idsTransportCompany = allAgu.agusBasicInfo.flatMap { it.transportCompanies.map { company -> company.id } }
		)

		cleanTest(client = client, idAGU = createdAGU2.cui, idDNO = dnoId)
	}

	@Test
	fun `get AGU by ID correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()

		// assert
		assertEquals(systemAGU.cui, createdAGU.cui)

		// clean
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id }
		)
	}

	@Test
	fun `get AGU by invalid ID should return not found`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()

		// act and assert
		getAGURequestWithStatusCode(client, "invalid", HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `get AGU by empty ID should return not found`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()

		// act and assert
		getAGURequestWithStatusCode(client, newTestAGU(dnoName = "").cui, HttpStatus.NOT_FOUND)
	}

	// TODO tests to getPredictionGasMeasures, getHourlyGasMeasures, getDailyGasMeasures, getTemperatureMeasures
}
