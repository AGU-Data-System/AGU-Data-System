package aguDataSystem.server.http

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AGUControllerTest {

	// One of the very few places where we use property injection
	@LocalServerPort
	var port: Int = 8080

}