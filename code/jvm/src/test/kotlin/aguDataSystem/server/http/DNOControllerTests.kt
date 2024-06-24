package aguDataSystem.server.http

import aguDataSystem.server.http.ControllerUtils.newTestDNO
import aguDataSystem.server.http.HTTPUtils.cleanTest
import aguDataSystem.server.http.HTTPUtils.createDNORequest
import aguDataSystem.server.http.HTTPUtils.createDNORequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.deleteDNORequest
import aguDataSystem.server.http.HTTPUtils.deleteDNORequestWithStatusCode
import aguDataSystem.server.http.HTTPUtils.getAllDNOsRequest
import aguDataSystem.server.http.HTTPUtils.toAllDNOResponse
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
class DNOControllerTests {

	@LocalServerPort
	private val port: Int = 8080

	private val baseURL = "http://localhost:$port/api"

	private val testTimeOut = Duration.ofHours(1)

	@Test
	fun `get all DNOs correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val createdDNO1 = createDNORequest(client, newTestDNO()).toDNOResponse()
		val createdDNO2 = createDNORequest(client, newTestDNO()).toDNOResponse()

		// act
		val allDNOs = getAllDNOsRequest(client).toAllDNOResponse()

		// assert
		assertTrue(allDNOs.dnos.any { it.id == createdDNO1.id })
		assertTrue(allDNOs.dnos.any { it.id == createdDNO2.id })

		// clean
		cleanTest(client = client, idDNO = createdDNO1.id)
		cleanTest(client = client, idDNO = createdDNO2.id)
	}

	@Test
	fun `add DNO correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dnoCreation = newTestDNO()

		// act
		val createdDNO = createDNORequest(client, dnoCreation).toDNOResponse()

		// assert
		val allDNOs = getAllDNOsRequest(client).toAllDNOResponse()
		assertTrue(allDNOs.dnos.any { it.id == createdDNO.id })

		// clean
		cleanTest(client = client, idDNO = createdDNO.id)
	}

	@Test
	fun `add DNO with duplicate name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dnoCreation = newTestDNO()
		createDNORequest(client, dnoCreation)

		// act and assert
		createDNORequestWithStatusCode(client, dnoCreation, HttpStatus.CONFLICT)
	}

	@Test
	fun `add DNO with empty name should fail`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dnoCreation = newTestDNO().copy(name = "")

		// act and assert
		createDNORequestWithStatusCode(client, dnoCreation, HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `delete DNO correctly`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()
		val dnoCreation = newTestDNO()
		val createdDNO = createDNORequest(client, dnoCreation).toDNOResponse()

		// act
		deleteDNORequest(client, createdDNO.id)

		// assert
		val allDNOs = getAllDNOsRequest(client).toAllDNOResponse()
		assertFalse(allDNOs.dnos.any { it.id == createdDNO.id })
	}

	@Test
	fun `delete DNO with invalid ID shouldn't do anything`() {
		// arrange
		val client = WebTestClient.bindToServer().baseUrl(baseURL).responseTimeout(testTimeOut).build()

		// act and assert
		deleteDNORequestWithStatusCode(client, Int.MIN_VALUE, HttpStatus.OK)
	}

}