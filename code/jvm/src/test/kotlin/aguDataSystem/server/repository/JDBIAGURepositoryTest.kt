package aguDataSystem.server.repository

import aguDataSystem.server.domain.contact.ContactCreation
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyDNO
import aguDataSystem.server.repository.RepositoryUtils.dummyGasLevels
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

		val dnoId = dnoRepo.addDNO(dummyDNO).id
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

		val dnoId = dnoRepo.addDNO(dummyDNO).id
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

		val dnoId = dnoRepo.addDNO(dummyDNO).id
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

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(name = "")

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add AGU with empty eic should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(eic = "")

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with same eic twice should trow exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU

		// act
		aguRepo.addAGU(agu, dnoId)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu.copy(cui = "PT6543210987654321XX", name = "another name"), dnoId)
		}
	}

	@Test
	fun `add agu with same cui twice should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU

		// act
		aguRepo.addAGU(agu, dnoId)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu.copy(name = "another name", eic = "Changed eic"), dnoId)
		}
	}

	@Test
	fun `add agu with same name twice should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU

		// act
		aguRepo.addAGU(agu, dnoId)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu.copy(eic = "Changed eic", cui = "PT6543210987654321XX"), dnoId)
		}
	}

	@Test
	fun `add agu with critical level over max level should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(critical = dummyGasLevels.max + 1))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with critical level over min level should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(critical = dummyGasLevels.min + 1))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with min level over max level should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(min = dummyGasLevels.max + 1))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with negative min level should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(min = -1))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with negative max level should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(max = -1))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with critical level over 100 should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(critical = 101))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with min level over 100 should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(min = 101))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with max level over 100 should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(max = 101))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with negative critical level should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(levels = dummyGasLevels.copy(critical = -1))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with negative load volume should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(loadVolume = -1)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with negative lower bound latitude should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(location = dummyAGU.location.copy(latitude = -91.0))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with negative lower bound longitude should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(location = dummyAGU.location.copy(longitude = -181.0))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with longitude over max degrees should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(location = dummyAGU.location.copy(longitude = 181.0))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with latitude over max degrees should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(location = dummyAGU.location.copy(latitude = 91.0))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with empty location name should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(location = dummyAGU.location.copy(name = ""))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with right training json should add agu`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(training = Json.encodeToString("json"))

		// act
		val result = aguRepo.addAGU(agu, dnoId)

		// assert
		assertNotNull(result)
		assertEquals(agu.cui, result)
	}

	@Test
	fun `add agu with bad training json should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(training = "not json")

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.addAGU(agu, dnoId)
		}
	}

	@Test
	fun `add agu with null training json should add agu`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU.copy(training = null)

		// act
		val result = aguRepo.addAGU(agu, dnoId)

		// assert
		assertNotNull(result)
		assertEquals(agu.cui, result)
	}

	@Test
	fun `get AGU by CUI`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id

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
		assertEquals(agu.isFavourite, aguFromDb.isFavourite)
		assertEquals(agu.notes, aguFromDb.notes)
		assertEquals(agu.training, aguFromDb.training)
		assertEquals(
			agu.contacts,
			aguFromDb.contacts.map { ContactCreation(name = it.name, phone = it.phone, type = it.type) })
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

		val dnoId = dnoRepo.addDNO(dummyDNO).id
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
		val sut2 = dummyAGU.copy(cui = "PT6543210987654321XX", eic = "another eic", name = "Test AGU 2")

		val dnoId = dnoRepo.addDNO(dummyDNO).id

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

		val dnoId = dnoRepo.addDNO(dummyDNO).id
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
	fun `update Favorite State with valid cui`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		aguRepo.updateFavouriteState(result, true)
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertTrue(aguFromDb.isFavourite)
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
	fun `update Active State with valid cui`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		aguRepo.updateActiveState(result, false)
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertFalse(aguFromDb.isFavourite)
	}

	@Test
	fun `update Active State with invalid cui`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)

		// act
		aguRepo.updateActiveState("invalid", false)
		val aguFromDb = aguRepo.getAGUByCUI("invalid")

		// assert
		assertNull(aguFromDb)
	}

	@Test
	fun `update gas levels of AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		val levels = dummyGasLevels
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
	fun `update gas levels of AGU with negative critical level should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val aguRepo = JDBIAGURepository(handle)
			val dnoRepo = JDBIDNORepository(handle)

			val dnoId = dnoRepo.addDNO(dummyDNO).id
			val agu = dummyAGU
			val result = aguRepo.addAGU(agu, dnoId)

			// act & assert
			assertFailsWith<UnableToExecuteStatementException> {
				aguRepo.updateGasLevels(result, dummyGasLevels.copy(critical = -1))
			}
		}

	@Test
	fun `update gas levels of AGU with negative min level should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val aguRepo = JDBIAGURepository(handle)
			val dnoRepo = JDBIDNORepository(handle)

			val dnoId = dnoRepo.addDNO(dummyDNO).id
			val agu = dummyAGU
			val result = aguRepo.addAGU(agu, dnoId)

			// act & assert
			assertFailsWith<UnableToExecuteStatementException> {
				aguRepo.updateGasLevels(result, dummyGasLevels.copy(min = -1))
			}
		}

	@Test
	fun `update gas levels of AGU with negative max level should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val aguRepo = JDBIAGURepository(handle)
			val dnoRepo = JDBIDNORepository(handle)

			val dnoId = dnoRepo.addDNO(dummyDNO).id
			val agu = dummyAGU
			val result = aguRepo.addAGU(agu, dnoId)

			// act & assert
			assertFailsWith<UnableToExecuteStatementException> {
				aguRepo.updateGasLevels(result, dummyGasLevels.copy(max = -1))
			}
		}

	@Test
	fun `update gas levels with critical level over min should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val aguRepo = JDBIAGURepository(handle)
			val dnoRepo = JDBIDNORepository(handle)

			val dnoId = dnoRepo.addDNO(dummyDNO).id
			val agu = dummyAGU
			val result = aguRepo.addAGU(agu, dnoId)

			// act & assert
			assertFailsWith<UnableToExecuteStatementException> {
				aguRepo.updateGasLevels(result, dummyGasLevels.copy(critical = dummyGasLevels.min + 1))
			}
		}

	@Test
	fun `update gas levels with critical level over max should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val aguRepo = JDBIAGURepository(handle)
			val dnoRepo = JDBIDNORepository(handle)

			val dnoId = dnoRepo.addDNO(dummyDNO).id
			val agu = dummyAGU
			val result = aguRepo.addAGU(agu, dnoId)

			// act & assert
			assertFailsWith<UnableToExecuteStatementException> {
				aguRepo.updateGasLevels(result, dummyGasLevels.copy(critical = dummyGasLevels.max + 1))
			}
		}

	@Test
	fun `update gas levels with min level over max should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateGasLevels(result, dummyGasLevels.copy(min = dummyGasLevels.max + 1))
		}
	}

	@Test
	fun `update gas levels with min level over 100 should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateGasLevels(result, dummyGasLevels.copy(min = 101))
		}
	}

	@Test
	fun `update gas levels with max level over 100 should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			aguRepo.updateGasLevels(result, dummyGasLevels.copy(max = 101))
		}
	}

	@Test
	fun `update gas levels with critical level over 100 should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val aguRepo = JDBIAGURepository(handle)
			val dnoRepo = JDBIDNORepository(handle)

			val dnoId = dnoRepo.addDNO(dummyDNO).id
			val agu = dummyAGU
			val result = aguRepo.addAGU(agu, dnoId)

			// act & assert
			assertFailsWith<UnableToExecuteStatementException> {
				aguRepo.updateGasLevels(result, dummyGasLevels.copy(critical = 101))
			}
		}

	@Test
	fun `update notes correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val dnoId = dnoRepo.addDNO(dummyDNO).id
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
		val dnoId = dnoRepo.addDNO(dummyDNO).id
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
		val dnoId = dnoRepo.addDNO(dummyDNO).id
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
		val dnoId = dnoRepo.addDNO(dummyDNO).id
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
		val dnoId = dnoRepo.addDNO(dummyDNO).id
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

		val dnoId = dnoRepo.addDNO(dummyDNO).id
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

		val dnoId = dnoRepo.addDNO(dummyDNO).id
		val agu = dummyAGU
		val result = aguRepo.addAGU(agu, dnoId)

		// act
		aguRepo.updateNotes(result, "")
		val aguFromDb = aguRepo.getAGUByCUI(result)

		// assert
		assertNotNull(aguFromDb)
		assertEquals("", aguFromDb.notes)
	}
}
