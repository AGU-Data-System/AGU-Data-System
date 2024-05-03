package aguDataSystem.server.repository

import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.contact.toContactType
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIAGURepositoryTest {

	private val dummyAGU =
		AGUBasicInfo(
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
			dnoName =  "DNO",
			isFavorite = false,
			notes = null,
			training = null,
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
			gasLevelUrl = "http://localhost:8080/agu/PT1234567890123456XX/gasLevel", // TODO dependency with fetcher service
		)

	@Test
	fun `add AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(dummyAGU.dnoName)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		 val result = aguRepo.addAGU(agu, dno.id)

		// assert
		 assertNotNull(result)
		 assertEquals(agu.cui, result)
	}
}