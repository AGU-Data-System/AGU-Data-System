package aguDataSystem.server.http

import aguDataSystem.server.http.ControllerUtils.dummyAGUCreationRequestModel
import aguDataSystem.server.http.HTTPUtils.createAGURequest
import java.time.Duration
import kotlin.test.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
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

		// act and assert
		createAGURequest(client, aguCreation)
	}
}