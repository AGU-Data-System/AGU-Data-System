package aguDataSystem.server.http

import aguDataSystem.server.http.ControllerUtils.newTestAGU
import aguDataSystem.server.http.ControllerUtils.newTestContact
import aguDataSystem.server.http.ControllerUtils.newTestDNO
import aguDataSystem.server.http.HTTPUtils.addContactRequest
import aguDataSystem.server.http.HTTPUtils.addContactRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.cleanTest
import aguDataSystem.server.http.HTTPUtils.createAGURequest
import aguDataSystem.server.http.HTTPUtils.createDNORequest
import aguDataSystem.server.http.HTTPUtils.deleteContactRequest
import aguDataSystem.server.http.HTTPUtils.deleteContactRequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.getAGURequest
import aguDataSystem.server.http.HTTPUtils.toAGUCreationResponse
import aguDataSystem.server.http.HTTPUtils.toAGUResponse
import aguDataSystem.server.http.HTTPUtils.toContactCreationResponse
import aguDataSystem.server.http.HTTPUtils.toDNOResponse
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactControllerTests {

	@LocalServerPort
	private val port: Int = 8080

	private val baseURL = "http://localhost:$port/api"

	private val testTimeOut = Duration.ofHours(1)

	@Test
	fun `add contact correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val contactCreation = newTestContact()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act
		val addedContact = addContactRequest(client, createdAGU.cui, contactCreation).toContactCreationResponse()

		// assert
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		assertTrue(systemAGU.contacts.contacts.any { it.id == addedContact.id })

		// clean
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}

	@Test
	fun `add contact with invalid AGU CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val contactCreation = newTestContact()

		// act and assert
		addContactRequestWithStatusCode(client, "invalid", contactCreation, HttpStatus.NOT_FOUND)
	}

	@Test
	fun `add contact with invalid contact data should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val contactCreation = newTestContact().copy(name = "")
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()

		// act and assert
		addContactRequestWithStatusCode(client, createdAGU.cui, contactCreation, HttpStatus.BAD_REQUEST)

		// clean
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}

	@Test
	fun `delete contact correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val contactCreation = newTestContact()
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val addedContact = addContactRequest(client, createdAGU.cui, contactCreation).toContactCreationResponse()

		// act
		deleteContactRequest(client, createdAGU.cui, addedContact.id)

		// assert
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		assertFalse(systemAGU.contacts.contacts.any { it.id == addedContact.id })

		// clean
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}

	@Test
	fun `delete contact with invalid AGU CUI should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val contactCreation = newTestContact()
		val dno = newTestDNO()
		val aguCreation = newTestAGU(dnoName = dno.name)
		val dnoId = createDNORequest(client, dno).toDNOResponse().id
		val createdAGU = createAGURequest(client, aguCreation).toAGUCreationResponse()
		val addedContact = addContactRequest(client, createdAGU.cui, contactCreation).toContactCreationResponse()

		// act and assert
		deleteContactRequestWithStatusCode(client, "invalid", addedContact.id, HttpStatus.NOT_FOUND)

		// clean
		val systemAGU = getAGURequest(client, createdAGU.cui).toAGUResponse()
		cleanTest(
			client = client,
			idAGU = systemAGU.cui,
			idDNO = dnoId,
			idsTransportCompany = systemAGU.transportCompanies.transportCompanies.map { it.id })
	}
}
