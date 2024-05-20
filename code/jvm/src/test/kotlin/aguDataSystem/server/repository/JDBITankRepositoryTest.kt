package aguDataSystem.server.repository

import aguDataSystem.server.repository.RepositoryUtils.DUMMY_DNO_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyTank
import aguDataSystem.server.repository.RepositoryUtils.toUpdateInfo
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.tank.JDBITankRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBITankRepositoryTest {

	@Test
	fun `add tank to AGU Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act
		tankRepo.addTank(agu.cui, tank)

		val tanks = tankRepo.getAGUTanks(agu.cui)

		// assert
		assert(tanks.isNotEmpty())
		assert(tanks.contains(tank))
	}

	@Test
	fun `add tank with invalid number of correction factor should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank.copy(correctionFactor = -1.0)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.addTank(agu.cui, tank)
		}
	}

	@Test
	fun `add tank with invalid levels should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank.copy(levels = dummyTank.levels.copy(min = 100, max = 50, critical = 100))

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.addTank(agu.cui, tank)
		}
	}

	@Test
	fun `add tank with levels over 100 should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank.copy(levels = dummyTank.levels.copy(min = 101, max = 101, critical = 101))

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.addTank(agu.cui, tank)
		}
	}

	@Test
	fun `add tank with invalid load volume should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank.copy(loadVolume = -1)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.addTank(agu.cui, tank)
		}
	}

	@Test
	fun `add tank with invalid capacity should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank.copy(capacity = -1)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.addTank(agu.cui, tank)
		}
	}

	@Test
	fun `add tank with invalid CUI should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val tankRepo = JDBITankRepository(handle)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.addTank("", dummyTank)
		}
	}

	@Test
	fun `add tank with invalid AGU should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val tankRepo = JDBITankRepository(handle)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.addTank("INVALID_CUI", dummyTank)
		}
	}

	@Test
	fun `add tank with invalid number should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank.copy(number = -1)

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.addTank(agu.cui, tank)
		}
	}

	@Test
	fun `get tanks by AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		tankRepo.addTank(agu.cui, tank)

		// act
		val tanks = tankRepo.getAGUTanks(agu.cui)

		// assert
		assert(tanks.isNotEmpty())
		assert(tanks.contains(tank))
	}

	@Test
	fun `get tanks from an empty AGU should return an empty list`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act
		val tanks = tankRepo.getAGUTanks(agu.cui)

		// assert
		assert(tanks.isEmpty())
	}

	@Test
	fun `get tanks with invalid CUI should return an empty list`() = testWithHandleAndRollback { handle ->
		// arrange
		val tankRepo = JDBITankRepository(handle)

		// act
		val tanks = tankRepo.getAGUTanks("INVALID_CUI")

		// assert
		assert(tanks.isEmpty())
	}

	@Test
	fun `get tank by number correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)

		// act
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, tankNumber)

		// assert
		assertNotNull(tankByNumber)
		assertEquals(tankByNumber, tank)
	}

	@Test
	fun `get tank by number from an empty AGU should return null`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, 1)

		// assert
		assertNull(tankByNumber)
	}

	@Test
	fun `get tank by number with wrong number should return null`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		tankRepo.addTank(agu.cui, tank)

		// act
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, Int.MIN_VALUE)

		// assert
		assertNull(tankByNumber)
	}

	@Test
	fun `get tank by number with invalid cui should return null`() = testWithHandleAndRollback { handle ->
		// arrange
		val tankRepo = JDBITankRepository(handle)

		// act
		val tankByNumber = tankRepo.getTankByNumber("", 1)

		// assert
		assertNull(tankByNumber)
	}

	@Test
	fun `delete tank correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)

		// act
		tankRepo.deleteTank(agu.cui, tankNumber)
		val tanks = tankRepo.getAGUTanks(agu.cui)

		// assert
		assert(tanks.isEmpty())
	}

	@Test
	fun `delete tank with invalid CUI does nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val tankRepo = JDBITankRepository(handle)

		// act & assert
		tankRepo.deleteTank("", 1)

	}

	@Test
	fun `delete tank with invalid number does nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		tankRepo.addTank(agu.cui, tank)

		// act & assert
		tankRepo.deleteTank(agu.cui, Int.MIN_VALUE)
	}

	@Test
	fun `update tank's capacity correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber, capacity = 50)

		// act
		tankRepo.updateTank(agu.cui, updatedTank.number, updatedTank.toUpdateInfo())
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, tankNumber)

		// assert
		assertNotNull(tankByNumber)
		assertEquals(tankByNumber, updatedTank)
	}

	@Test
	fun `update tank's capacity incorrectly should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber, capacity = -1)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.updateTank(agu.cui, updatedTank.number, updatedTank.toUpdateInfo())
		}
	}

	@Test
	fun `update tank's number correctly should do nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber + 1)

		// act
		tankRepo.updateTank(agu.cui, updatedTank.number, tank.toUpdateInfo())
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, updatedTank.number)

		// assert
		assertNull(tankByNumber)
		assertNotEquals(tankByNumber, tank)
	}

	@Test
	fun `update tank's number incorrectly should do nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = Int.MIN_VALUE)

		// act
		tankRepo.updateTank(agu.cui, updatedTank.number, tank.toUpdateInfo())
		val updatedTankFromDB = tankRepo.getTankByNumber(agu.cui, updatedTank.number)

		// assert
		assertNull(updatedTankFromDB)
	}

	@Test
	fun `update tank's correction factor correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber, correctionFactor = 1.0)

		// act
		tankRepo.updateTank(agu.cui, updatedTank.number, updatedTank.toUpdateInfo())
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, updatedTank.number)

		// assert
		assertNotNull(tankByNumber)
		assertEquals(tankByNumber, updatedTank)
	}

	@Test
	fun `update tank with invalid number of correction factor should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber, correctionFactor = -1.0)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.updateTank(agu.cui, updatedTank.number, updatedTank.toUpdateInfo())
		}
	}

	@Test
	fun `update tank's levels correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber, levels = tank.levels.copy(max = 100))

		// act
		tankRepo.updateTank(agu.cui, updatedTank.number, updatedTank.toUpdateInfo())
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, updatedTank.number)

		// assert
		assertNotNull(tankByNumber)
		assertEquals(tankByNumber, updatedTank)
	}

	@Test
	fun `update tank with invalid levels should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber, levels = tank.levels.copy(min = 100, max = 50, critical = 100))

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.updateTank(agu.cui, updatedTank.number, updatedTank.toUpdateInfo())
		}
	}

	@Test
	fun `update tank's load volume correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber, loadVolume = 100)

		// act
		tankRepo.updateTank(agu.cui, updatedTank.number, updatedTank.toUpdateInfo())
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, updatedTank.number)

		// assert
		assertNotNull(tankByNumber)
		assertEquals(tankByNumber, updatedTank)
	}

	@Test
	fun `update tank with invalid load volume should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		val tankNumber = tankRepo.addTank(agu.cui, tank)
		val updatedTank = tank.copy(number = tankNumber, loadVolume = -1)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			tankRepo.updateTank(agu.cui, updatedTank.number, updatedTank.toUpdateInfo())
		}
	}

	@Test
	fun `get number of tanks correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU
		val tank = dummyTank

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)
		tankRepo.addTank(agu.cui, tank)

		// act
		val numberOfTanks = tankRepo.getNumberOfTanks(agu.cui)

		// assert
		assertEquals(1, numberOfTanks)
	}

	@Test
	fun `get number of tanks from an empty AGU should return 0`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepo = JDBIDNORepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val tankRepo = JDBITankRepository(handle)
		val agu = dummyAGU

		val dnoId = dnoRepo.addDNO(DUMMY_DNO_NAME)
		aguRepo.addAGU(agu, dnoId)

		// act
		val numberOfTanks = tankRepo.getNumberOfTanks(agu.cui)

		// assert
		assertEquals(0, numberOfTanks)
	}
}
