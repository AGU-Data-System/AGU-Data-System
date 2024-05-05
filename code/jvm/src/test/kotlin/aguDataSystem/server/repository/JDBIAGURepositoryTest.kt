package aguDataSystem.server.repository

import aguDataSystem.server.repository.RepositoryUtils.DUMMY_DNO_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
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

	@Test
	fun `add AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dnoId)

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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)

		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		aguRepo.addAGU(agu, dnoId)

		// act
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
		dnoRepo.addDNO(DUMMY_DNO_NAME)

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
		dnoRepo.addDNO(DUMMY_DNO_NAME)

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
	fun `update AGU name correctly should update name`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(name = "Updated Name")

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.name, updatedAGUFromDb.name)
	}

	@Test
	fun `update AGU name for an already used name should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu1 = dummyAGU
		val agu2 = dummyAGU.copy(cui = "PT6543210987654321XX", name = "Test AGU 2")

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		// act
		aguRepo.addAGU(agu1, dno.id)
		val result = aguRepo.addAGU(agu2, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(name = agu1.name)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU levels correctly should update levels`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(levels = aguFromDb.levels.copy(min = 10, max = 90, critical = 30))

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.levels, updatedAGUFromDb.levels)
	}

	@Test
	fun `update AGU should fail if critical level under min level`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(levels = aguFromDb.levels.copy(critical = 10))

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
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(levels = aguFromDb.levels.copy(critical = 90))

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU DNO for a valid DNO should update DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)
		dnoRepo.addDNO("DNO 2")

		val dno1 = dnoRepo.getByName(dummyAGU.dnoName)
		val dno2 = dnoRepo.getByName("DNO 2")
		requireNotNull(dno1)
		requireNotNull(dno2)

		// act
		val result = aguRepo.addAGU(agu, dno1.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(dno = dno2)

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.dno, updatedAGUFromDb.dno)
	}

	@Test
	fun `update AGU should fail if DNO updated to an un-existing DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(dno = dno.copy(id = Int.MAX_VALUE))

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `updating AGU location correctly should update location`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(location = aguFromDb.location.copy(latitude = 50.0, longitude = 50.0))

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.location, updatedAGUFromDb.location)
	}

	@Test
	fun `update AGU should fail if latitude is out of range`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(location = aguFromDb.location.copy(latitude = 91.0))

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
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(location = aguFromDb.location.copy(longitude = 181.0))

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU load volume correctly should update load volume`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(loadVolume = 70)

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.loadVolume, updatedAGUFromDb.loadVolume)
	}

	@Test
	fun `update AGU should fail if load volume is negative`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(loadVolume = -1)

		// assert
		assertFailsWith<IllegalStateException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update isFavorite AGU should update isFavorite`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(isFavorite = true)

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.isFavorite, updatedAGUFromDb.isFavorite)
	}

	@Test
	fun `update AGU notes correctly should update notes`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(notes = "Updated notes")

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.notes, updatedAGUFromDb.notes)
	}

	@Test
	fun `update AGU training correctly should update training`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(training = "Updated training")

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.training, updatedAGUFromDb.training)
	}

	@Test
	fun `update AGU image correctly should update image`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		// add DNO
		dnoRepo.addDNO(DUMMY_DNO_NAME)

		val dno = dnoRepo.getByName(dummyAGU.dnoName)
		requireNotNull(dno)

		val agu = dummyAGU

		// act
		val result = aguRepo.addAGU(agu, dno.id)
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(image = ByteArray(1) { 1.toByte() })

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.image, updatedAGUFromDb.image)
	}
}
