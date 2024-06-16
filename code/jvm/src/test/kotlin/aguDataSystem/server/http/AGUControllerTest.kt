package aguDataSystem.server.http

import aguDataSystem.server.http.ControllerUtils.dummyAGUCreationRequestModel
import aguDataSystem.server.http.ControllerUtils.dummyDNOCreationRequestModel
import aguDataSystem.server.http.HTTPUtils.cleanTest
import aguDataSystem.server.http.HTTPUtils.createAGURequest
import aguDataSystem.server.http.HTTPUtils.createAGURequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.createDNORequest
import aguDataSystem.server.http.HTTPUtils.getAGURequest
import aguDataSystem.server.http.HTTPUtils.toAGUCreationResponse
import aguDataSystem.server.http.HTTPUtils.toAGUResponse
import aguDataSystem.server.http.HTTPUtils.toDNOResponse
import java.time.Duration
import kotlin.test.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AGUControllerTest {

	// One of the very few places where we use property injection
	@LocalServerPort
	private val port: Int = 8080

	private val baseURL = "http://localhost:$port/api"

	private val testTimeOut = Duration.ofHours(1)

	@Test
	fun `create AGU properly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
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
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
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
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(cui = "invalid", eic = "newEIC", name = "newName")

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)


		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with empty cui should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(cui = "", eic = "newEIC", name = "newName")

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with already used cui should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		createDNORequest(client, dno)
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val sut = dummyAGUCreationRequestModel.copy(cui = aguCreation.cui, eic = "newEIC", name = "newName")

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
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(eic = "")

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with already used EIC should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		createDNORequest(client, dno)
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val sut = dummyAGUCreationRequestModel.copy(cui = "PT6543210987654321XX", eic = aguCreation.eic, name = "newName")

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
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(name = "")

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with the same name twice should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		createDNORequest(client, dno)
		createAGURequest(client, aguCreation)
		val sut = dummyAGUCreationRequestModel.copy(cui = "PT6543210987654321XX", eic = "newEIC")

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
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(minLevel = -1)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(maxLevel = -1)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(criticalLevel = -1)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with critical over min level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(minLevel = dummyAGUCreationRequestModel.criticalLevel - 1)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with critical over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(criticalLevel = dummyAGUCreationRequestModel.maxLevel + 1)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with min level over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(minLevel = dummyAGUCreationRequestModel.maxLevel + 1)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid load Volume should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(loadVolume = -1.0)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid latitude should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(latitude = -91.0)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid longitude should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(longitude = -181.0)

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with empty DNO name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(dnoName = "")

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with un existing DNO should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val sut = dummyAGUCreationRequestModel.copy(dnoName = "un existing")

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		// no clean needed
	}

	@Test
	fun `create AGU with invalid gas URL should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(gasLevelUrl = "invalid")

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid contact type should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(contacts = dummyAGUCreationRequestModel.contacts.map { it.copy(type = "invalid") })

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid contact phone should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(contacts = dummyAGUCreationRequestModel.contacts.map { it.copy(phone = "invalid") })

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid contact name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = dummyAGUCreationRequestModel.copy(contacts = dummyAGUCreationRequestModel.contacts.map { it.copy(name = "") })

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU without any tank should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = emptyList())

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank number should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(number = -1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank capacity should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(capacity = -1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank min level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(minLevel = -1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(maxLevel = -1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(criticalLevel = -1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with tank critical level over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(criticalLevel = aguCreation.tanks.first().maxLevel + 1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with tank min level over max level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(minLevel = aguCreation.tanks.first().maxLevel + 1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with tank min level over critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(minLevel = aguCreation.tanks.first().criticalLevel + 1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with tank max level under critical level should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(maxLevel = aguCreation.tanks.first().criticalLevel - 1)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with invalid tank load volume should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(tanks = listOf(aguCreation.tanks.first().copy(loadVolume = -1.0)))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with un existing transport companies should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(transportCompanies = listOf("un existing"))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}

	@Test
	fun `create AGU with empty transport company name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val sut = aguCreation.copy(transportCompanies = listOf(""))

		// act and assert
		createAGURequestWithStatusCode(client, sut, HttpStatus.BAD_REQUEST)

		// clean
		cleanTest(client = client, idDNO = dnoId)
	}
}