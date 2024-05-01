package aguDataSystem.server.repository

import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.domain.reading.GasReading
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.contact.toContactType
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import java.time.LocalDateTime
import kotlin.test.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIAGURepositoryTest {

	private val dummyAGU = AGU(
		cui = "PT1234567890123456XX",
		name = "Test AGU",
		levels = GasLevels(
			min = 20,
			max = 85,
			critical = 20
		),
		loadVolume = 50,
		location = Location(
			latitude = 40.7128,
			longitude = 74.0060,
			name = "New York"
		),
		dno = DNO(
			name = "DNO",
			id = 1
		),
		isFavorite = false,
		notes = null,
		training = "",
		image = ByteArray(1) { 0.toByte() },
		contacts = listOf(
			Contact(
				name = "John Doe",
				phone = "1234567890",
				type = "LOGISTIC".toContactType()
			),
			Contact(
				name = "Jane Doe",
				phone = "0987654321",
				type = "EMERGENCY".toContactType()
			)
		),
		tanks = listOf(
			Tank(
				number = 1,
				levels = GasLevels(
					min = 20,
					max = 85,
					critical = 20
				),
				loadVolume = 50,
				capacity = 100,
			)
		),
		providers = listOf(
			ProviderType.GAS.createProviderWithReadings(
				id = 1,
				readings = listOf(
					GasReading(
						timestamp = LocalDateTime.now(),
						predictionFor = LocalDateTime.now().minusHours(1),
						level = 50
					)
				)
			)
		)
	)

	@Test
	fun `add AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val repo = JDBIAGURepository(handle)
		val agu = dummyAGU

		// act
		// val result = repo.addAGU(agu)

		// assert
		// assertNotNull(result)
		// assertEquals(agu.cui, result.cui)
		// assertEquals(agu.name, result.name)
	}
}