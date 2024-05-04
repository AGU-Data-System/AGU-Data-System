package aguDataSystem.server.repository

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.toContactType
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIAGURepositoryTest {

	private val dummyAGU = AGUBasicInfo(
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
		dnoName = "DNO",
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
				number = 0,
				levels = GasLevels(
					min = 20,
					max = 85,
					critical = 20
				),
				loadVolume = 50,
				capacity = 100,
			)
		),
		gasLevelUrl = "http://localhost:8081/api/agu/PT1234567890123456XX/gasLevel",
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

	@Test
	fun `add agu twice should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(dummyAGU.dnoName)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		aguRepo.addAGU(agu, dno.id)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dno.id)
		}
	}

	@Test
	fun `add agu without DNO should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)

		val agu = dummyAGU

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, Int.MAX_VALUE)
		}
	}

	@Test
	fun `get AGU by CUI`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(dummyAGU.dnoName)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dno.id)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertEquals(agu.cui, aguFromDb.cui)
		assertEquals(agu.name, aguFromDb.name)
		assertEquals(agu.levels, aguFromDb.levels)
		assertEquals(agu.loadVolume, aguFromDb.loadVolume)
		assertEquals(agu.location, aguFromDb.location)
		assertEquals(agu.dnoName, aguFromDb.dno.name)
		assertEquals(agu.isFavorite, aguFromDb.isFavorite)
		assertEquals(agu.notes, aguFromDb.notes)
		assertEquals(agu.training, aguFromDb.training)
		assertEquals(agu.image, aguFromDb.image)
		assertEquals(agu.contacts, aguFromDb.contacts)
		assertEquals(agu.tanks, aguFromDb.tanks)
	}

	@Test
	fun `get AGU by CUI with invalid CUI`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)

		// act
		val result = aguRepo.getAGUByCUI("invalid")

		// assert
		assertNull(result)
	}

	@Test
	fun `get AGU by name`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(dummyAGU.dnoName)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getCUIByName(agu.name)

		// assert
		assertNotNull(aguFromDb)
		assertEquals(agu.cui, aguFromDb)
	}

	@Test
	fun `get AGU by name with invalid name`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)

		// act
		val result = aguRepo.getCUIByName("invalid")

		// assert
		assertNull(result)
	}

	@Test
	fun `get all AGUs`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val sut1 = dummyAGU
		val sut2 = dummyAGU.copy(cui = "PT6543210987654321XX", name = "Test AGU 2")

		// add DNO
		dnoRepo.addDNO(dummyAGU.dnoName)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)


		// act
		aguRepo.addAGU(sut1, dno.id)
		aguRepo.addAGU(sut2, dno.id)
		val aguList = aguRepo.getAGUs()

		// assert
		assertEquals(2, aguList.size)
		assertEquals(sut1.cui, aguList[0].cui)
		assertEquals(sut2.cui, aguList[1].cui)
	}

	@Test
	fun `get empty AGU list`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)

		// act
		val aguList = aguRepo.getAGUs()

		// assert
		assertEquals(0, aguList.size)
	}

	@Test
	fun `isAGUStored should be true`() = testWithHandleAndRollback { handle ->
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
		val isStored = aguRepo.isAGUStored(result)

		// assert
		assertTrue(isStored)
	}

	@Test
	fun `isAGUStored should be false`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)

		// act
		val isStored = aguRepo.isAGUStored("invalid")

		// assert
		assertFalse(isStored)
	}

	@Test
	fun `update AGU should fail if critical level under min level`() = testWithHandleAndRollback { handle ->
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
		val aguFromDb = aguRepo.getAGUByCUI(result)
		val updatedAGU = aguFromDb!!.copy(levels = aguFromDb.levels.copy(critical = 10))

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU should fail if critical level over max level`() = testWithHandleAndRollback { handle ->
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
		val aguFromDb = aguRepo.getAGUByCUI(result)
		val updatedAGU = aguFromDb!!.copy(levels = aguFromDb.levels.copy(critical = 90))

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU should fail if DNO updated to an un-existing DNO`() = testWithHandleAndRollback { handle ->
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
		val aguFromDb = aguRepo.getAGUByCUI(result)
		val updatedAGU = aguFromDb!!.copy(dno = dno.copy(id = Int.MAX_VALUE))

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU should fail if latitude is out of range`() = testWithHandleAndRollback { handle ->
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
		val aguFromDb = aguRepo.getAGUByCUI(result)
		val updatedAGU = aguFromDb!!.copy(location = aguFromDb.location.copy(latitude = 91.0))

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU should fail if longitude is out of range`() = testWithHandleAndRollback { handle ->
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
		val aguFromDb = aguRepo.getAGUByCUI(result)
		val updatedAGU = aguFromDb!!.copy(location = aguFromDb.location.copy(longitude = 181.0))

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU should fail if load volume is negative`() = testWithHandleAndRollback { handle ->
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
		val aguFromDb = aguRepo.getAGUByCUI(result)
		val updatedAGU = aguFromDb!!.copy(loadVolume = -1)

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}
}
