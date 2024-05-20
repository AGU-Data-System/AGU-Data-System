package aguDataSystem.server.repository

import aguDataSystem.server.domain.contact.ContactCreation
import aguDataSystem.server.domain.gasLevels.GasLevels
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
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
	fun `add AGU with empty CUI should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU.copy(cui = "")

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add AGU with empty name should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU.copy(name = "")

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
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
		assertEquals(agu.contacts, aguFromDb.contacts.map { ContactCreation(name = it.name, phone = it.phone, type = it.type) })
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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)

		aguRepo.addAGU(sut1, dnoId)
		aguRepo.addAGU(sut2, dnoId)

		// act
		val aguList = aguRepo.getAGUsBasicInfo()

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
		val aguList = aguRepo.getAGUsBasicInfo()

		// assert
		assertEquals(0, aguList.size)
	}

	@Test
	fun `isAGUStored should be true`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu1, dnoId)
		val result = aguRepo.addAGU(agu2, dnoId)

		// act
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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(levels = aguFromDb.levels.copy(min = 30, max = 90, critical = 10))

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.levels, updatedAGUFromDb.levels)
	}

	@Test
	fun `update AGU should fail if min level under critical level`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(levels = aguFromDb.levels.copy(min = 0))

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU should fail if critical level over max level`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(levels = aguFromDb.levels.copy(critical = 90))

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU DNO for a valid DNO should update DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId1 = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val dnoId2 = dnoRepo.addDNO("DNO 2")

		val dno1 = dnoRepo.getById(dnoId1)
		val dno2 = dnoRepo.getById(dnoId2)
		requireNotNull(dno1)
		requireNotNull(dno2)

		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId1)

		// act
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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val dno = dnoRepo.getById(dnoId)
		requireNotNull(dno)

		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dno.id)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(dno = dno.copy(id = Int.MAX_VALUE))

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `updating AGU location correctly should update location`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(location = aguFromDb.location.copy(latitude = 91.0))

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU should fail if longitude is out of range`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(location = aguFromDb.location.copy(longitude = 181.0))

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update AGU load volume correctly should update load volume`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
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

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(loadVolume = -1)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateAGU(updatedAGU)
		}
	}

	@Test
	fun `update isFavorite AGU should update isFavorite`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
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
		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(notes = "Updated notes")

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.notes, updatedAGUFromDb.notes)
	}

	@Test
	fun `update AGU notes with empty notes should update notes`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(notes = "")

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.notes, updatedAGUFromDb.notes)
	}

	@Test
	fun `update AGU notes with null notes should update notes`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(notes = null)

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.notes, updatedAGUFromDb.notes)
	}

	@Test
	fun `update AGU training correctly should update training`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(training = Json.encodeToString("Updated training"))

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.training, updatedAGUFromDb.training)
	}

	@Test
	fun `update AGU training with null should update training`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(training = null)

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.training, updatedAGUFromDb.training)
	}

	@Test
	fun `update AGU image correctly should update image`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(image = ByteArray(1) { 1.toByte() })

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.image, updatedAGUFromDb.image)
	}

	@Test
	fun `update AGU image with empty image should update image`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getAGUByCUI(result)
		requireNotNull(aguFromDb)
		val updatedAGU = aguFromDb.copy(image = ByteArray(0) { 0.toByte() })

		// assert
		val updatedAGUFromDb = aguRepo.updateAGU(updatedAGU)
		assertEquals(updatedAGU.image, updatedAGUFromDb.image)
	}

	@Test
	fun `update Favorite State with valid cui`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		aguRepo.updateFavouriteState(result, true)
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertTrue(aguFromDb.isFavorite)
	}

	@Test
	fun `update Favorite State with invalid cui`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)

		// act
		aguRepo.updateFavouriteState("invalid", true)
		val aguFromDb = aguRepo.getAGUByCUI("invalid")

		// assert
		assertNull(aguFromDb)
	}

	@Test
	fun `update gas levels of AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val levels = GasLevels(10, 20, 0)
		aguRepo.updateGasLevels(result, levels)
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertEquals(levels, aguFromDb.levels)
	}

	@Test
	fun `update gas levels of AGU with invalid CUI should do nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val cui = "invalid"

		// act
		aguRepo.updateGasLevels(cui, GasLevels(10, 20, 30))

		// assert
		assertNull(aguRepo.getAGUByCUI(cui))
	}

	@Test
	fun `update gas levels of AGU with negative critical level should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateGasLevels(result, GasLevels(0, 20, -1))
		}
	}

	@Test
	fun `update notes correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val notes = "Updated notes"
		aguRepo.updateNotes(result, notes)
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertEquals(notes, aguFromDb.notes)
	}

	@Test
	fun `update notes with invalid CUI should do nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val cui = "invalid"

		// act
		aguRepo.updateNotes(cui, "Updated notes")

		// assert
		assertNull(aguRepo.getAGUByCUI(cui))
	}

	@Test
	fun `update training correctly should update training`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val training = Json.encodeToString("Updated training")
		aguRepo.updateTrainingModel(result, training)
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertEquals(training, aguFromDb.training)
	}

	@Test
	fun `update training with null should update training`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		aguRepo.updateTrainingModel(result, null)
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertNull(aguFromDb.training)
	}

	@Test
	fun `update training with empty training should update training`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		aguRepo.updateTrainingModel(result, Json.encodeToString(""))
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertEquals("\"\"", aguFromDb.training.toString())
	}

	@Test
	fun `update training with bad json should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val result = aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateTrainingModel(result, "bad json")
		}
	}

	@Test
	fun `update notes with null should update notes`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		aguRepo.updateNotes(result, null)
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertNull(aguFromDb.notes)
	}

	@Test
	fun `update notes with empty notes should update notes`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		aguRepo.updateNotes(result, "")
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertEquals("", aguFromDb.notes)
	}

	@Test
	fun `get Favourite AGU`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		val agu = dummyAGU.copy(isFavorite = true)
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val aguFromDb = aguRepo.getFavouriteAGUs()

		// assert
		assertNotNull(aguFromDb)
		assertEquals(1, aguFromDb.size)
		assert(aguFromDb.any { result == it.cui })
	}
}
