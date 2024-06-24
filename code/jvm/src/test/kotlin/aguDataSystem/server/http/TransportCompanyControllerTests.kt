package aguDataSystem.server.http

import aguDataSystem.server.http.ControllerUtils.newTestAGU
import aguDataSystem.server.http.ControllerUtils.newTestDNO
import aguDataSystem.server.http.ControllerUtils.newTransportCompany
import aguDataSystem.server.http.HTTPUtils.addTransportCompanyRequest
import aguDataSystem.server.http.HTTPUtils.addTransportCompanyRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.addTransportCompanyToAGURequest
import aguDataSystem.server.http.HTTPUtils.addTransportCompanyToAGURequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.cleanTest
import aguDataSystem.server.http.HTTPUtils.createAGURequest
import aguDataSystem.server.http.HTTPUtils.createDNORequest
import aguDataSystem.server.http.HTTPUtils.deleteTransportCompanyRequest
import aguDataSystem.server.http.HTTPUtils.deleteTransportCompanyRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.getAllTransportCompaniesRequest
import aguDataSystem.server.http.HTTPUtils.getTransportCompaniesOfAGURequest
import aguDataSystem.server.http.HTTPUtils.removeTransportCompanyFromAGURequest
import aguDataSystem.server.http.HTTPUtils.removeTransportCompanyFromAGURequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.toAGUCreationResponse
import aguDataSystem.server.http.HTTPUtils.toDNOResponse
import aguDataSystem.server.http.HTTPUtils.toTransportCompanyCreationResponse
import aguDataSystem.server.http.HTTPUtils.toTransportCompanyListResponse
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransportCompanyControllerTests {

	@LocalServerPort
	private val port: Int = 8080

	private val baseURL = "http://localhost:$port/api"

	private val testTimeOut = Duration.ofHours(1)

	@Test
	fun `get all transport companies correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()

		// act
		val response = getAllTransportCompaniesRequest(client).toTransportCompanyListResponse()

		// assert
		assertTrue(response.transportCompanies.isNotEmpty())
	}

	@Test
	fun `add transport company correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val transportCompanyCreation = newTransportCompany()

		// act
		val createdTransportCompany =
			addTransportCompanyRequest(client, transportCompanyCreation).toTransportCompanyCreationResponse()

		// assert
		assertNotEquals(createdTransportCompany.id, 0)

		// clean
		deleteTransportCompanyRequest(client, createdTransportCompany.id)
	}

	@Test
	fun `add transport company with invalid name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val transportCompanyCreation = newTransportCompany().copy(name = "")

		// act and assert
		addTransportCompanyRequestWithStatusCode(client, transportCompanyCreation, HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `delete transport company correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val transportCompanyCreation = newTransportCompany()
		val createdTransportCompany =
			addTransportCompanyRequest(client, transportCompanyCreation).toTransportCompanyCreationResponse()
		// act
		deleteTransportCompanyRequestWithStatusCode(client, createdTransportCompany.id, HttpStatus.OK)

		// assert
		val allTransportCompanies = getAllTransportCompaniesRequest(client).toTransportCompanyListResponse()
		assertTrue(allTransportCompanies.transportCompanies.none { it.id == createdTransportCompany.id })
	}

	@Test
	fun `add transport company to AGU correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val transportCompanyCreation = newTransportCompany()
		val createdTransportCompany =
			addTransportCompanyRequest(client, transportCompanyCreation).toTransportCompanyCreationResponse()

		// act
		addTransportCompanyToAGURequest(client, createdAGU.cui, createdTransportCompany.id)

		// assert
		val aguTransportCompanies =
			getTransportCompaniesOfAGURequest(client, createdAGU.cui).toTransportCompanyListResponse()
		assertTrue(aguTransportCompanies.transportCompanies.any { it.id == createdTransportCompany.id })

		// clean
		deleteTransportCompanyRequest(client, createdTransportCompany.id)
		cleanTest(client, idAGU = createdAGU.cui, idDNO = dnoId)
	}

	@Test
	fun `remove transport company from AGU correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val transportCompanyCreation = newTransportCompany()
		val createdTransportCompany =
			addTransportCompanyRequest(client, transportCompanyCreation).toTransportCompanyCreationResponse()
		addTransportCompanyToAGURequest(client, createdAGU.cui, createdTransportCompany.id)

		// act
		removeTransportCompanyFromAGURequest(client, createdAGU.cui, createdTransportCompany.id)

		// assert
		val aguTransportCompanies =
			getTransportCompaniesOfAGURequest(client, createdAGU.cui).toTransportCompanyListResponse()
		assertTrue(aguTransportCompanies.transportCompanies.none { it.id == createdTransportCompany.id })

		// clean
		deleteTransportCompanyRequest(client, createdTransportCompany.id)
		cleanTest(client, idAGU = createdAGU.cui, idDNO = dnoId)
	}

	@Test
	fun `add transport company to AGU with invalid CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val transportCompanyCreation = newTransportCompany()
		val createdTransportCompany =
			addTransportCompanyRequest(client, transportCompanyCreation).toTransportCompanyCreationResponse()


		// act and assert
		addTransportCompanyToAGURequestWithStatusCode(
			client,
			"invalid",
			createdTransportCompany.id,
			HttpStatus.NOT_FOUND
		)

		// clean
		deleteTransportCompanyRequest(client, createdTransportCompany.id)
	}

	@Test
	fun `add transport company to AGU with invalid transport company ID should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		addTransportCompanyToAGURequestWithStatusCode(client, createdAGU.cui, -1, HttpStatus.NOT_FOUND)

		// clean
		cleanTest(client, idAGU = createdAGU.cui, idDNO = dnoId)
	}

	@Test
	fun `remove transport company from AGU with invalid CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val transportCompanyCreation = newTransportCompany()
		val createdTransportCompany =
			addTransportCompanyRequest(client, transportCompanyCreation).toTransportCompanyCreationResponse()

		// act and assert
		removeTransportCompanyFromAGURequestWithStatusCode(
			client,
			"invalid",
			createdTransportCompany.id,
			HttpStatus.NOT_FOUND
		)

		// clean
		deleteTransportCompanyRequest(client, createdTransportCompany.id)
	}

	@Test
	fun `remove transport company from AGU with invalid transport company ID should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		removeTransportCompanyFromAGURequestWithStatusCode(client, createdAGU.cui, -1, HttpStatus.NOT_FOUND)

		// clean
		cleanTest(client, idAGU = createdAGU.cui, idDNO = dnoId)
	}
}
