package aguDataSystem.server.http

import aguDataSystem.server.http.ControllerUtils.dummyAGUCreationRequestModel
import aguDataSystem.server.http.ControllerUtils.dummyDNOCreationRequestModel
import aguDataSystem.server.http.ControllerUtils.dummyTankCreationRequestModel
import aguDataSystem.server.http.ControllerUtils.dummyTankUpdateRequestModel
import aguDataSystem.server.http.HTTPUtils.addTankRequest
import aguDataSystem.server.http.HTTPUtils.addTankRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.cleanTest
import aguDataSystem.server.http.HTTPUtils.createAGURequest
import aguDataSystem.server.http.HTTPUtils.createDNORequest
import aguDataSystem.server.http.HTTPUtils.deleteTankRequest
import aguDataSystem.server.http.HTTPUtils.deleteTankRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.getAGURequest
import aguDataSystem.server.http.HTTPUtils.toAGUCreationResponse
import aguDataSystem.server.http.HTTPUtils.toAGUResponse
import aguDataSystem.server.http.HTTPUtils.toDNOResponse
import aguDataSystem.server.http.HTTPUtils.toTankResponse
import aguDataSystem.server.http.HTTPUtils.updateTankRequest
import aguDataSystem.server.http.HTTPUtils.updateTankRequestWithStatusCode
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TankControllerTests {

	@LocalServerPort
	private val port: Int = 8080

	private val baseURL = "http://localhost:$port/api"

	private val testTimeOut = Duration.ofHours(1)

	@Test
	fun `add tank correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val tankCreation = dummyTankCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act
		val addedTank = addTankRequest(client, createdAGU.cui, tankCreation).toTankResponse()

		// assert
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		assertTrue(systemAGU.tanks.tanks.any { it.number == addedTank.number })

		// clean
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}

	@Test
	fun `add tank with invalid AGU CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val tankCreation = dummyTankCreationRequestModel

		// act and assert
		addTankRequestWithStatusCode(client, "invalid", tankCreation, HttpStatus.NOT_FOUND)
	}

	@Test
	fun `add tank with invalid tank data should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val tankCreation = dummyTankCreationRequestModel.copy(number = -1)
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		addTankRequestWithStatusCode(client, createdAGU.cui, tankCreation, HttpStatus.BAD_REQUEST)

		// clean
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}

	@Test
	fun `update tank correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val tankCreation = dummyTankCreationRequestModel
		val tankUpdate = dummyTankUpdateRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val addedTank = addTankRequest(client, createdAGU.cui, tankCreation).toTankResponse()

		// act
		val updatedAGU =
			updateTankRequest(client, createdAGU.cui, addedTank.number.toString(), tankUpdate).toAGUResponse()

		// assert
		assertNotEquals(updatedAGU.tanks.tanks.find { it.number == addedTank.number }?.capacity, addedTank.capacity)

		// clean
		cleanTest(
			client = client,
			idAGU = updatedAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = updatedAGU.transportCompanies.transportCompanies.map { it.id })
	}

	@Test
	fun `update tank with invalid AGU CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val tankUpdate = dummyTankUpdateRequestModel

		// act and assert
		updateTankRequestWithStatusCode(client, "invalid", "1", tankUpdate, HttpStatus.NOT_FOUND)
	}

	@Test
	fun `update tank with invalid tank data should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val tankCreation = dummyTankCreationRequestModel
		val tankUpdate = dummyTankUpdateRequestModel.copy(capacity = -1)
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val addedTank = addTankRequest(client, createdAGU.cui, tankCreation).toTankResponse()

		// act and assert
		updateTankRequestWithStatusCode(
			client,
			createdAGU.cui,
			addedTank.number.toString(),
			tankUpdate,
			HttpStatus.BAD_REQUEST
		)

		// clean
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}

	@Test
	fun `delete tank correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val tankCreation = dummyTankCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val addedTank = addTankRequest(client, createdAGU.cui, tankCreation).toTankResponse()

		// act
		deleteTankRequest(client, createdAGU.cui, addedTank.number)

		// assert
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		assertFalse(systemAGU.tanks.tanks.any { it.number == addedTank.number })

		// clean
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}

	@Test
	fun `delete tank with invalid AGU CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()

		// act and assert
		deleteTankRequestWithStatusCode(client, "invalid", Int.MIN_VALUE, HttpStatus.NOT_FOUND)
	}

	@Test
	fun `delete tank with invalid tank number should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val aguCreation = dummyAGUCreationRequestModel
		val dno = dummyDNOCreationRequestModel
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		deleteTankRequestWithStatusCode(client, createdAGU.cui, -1, HttpStatus.BAD_REQUEST)

		// clean
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}
}
