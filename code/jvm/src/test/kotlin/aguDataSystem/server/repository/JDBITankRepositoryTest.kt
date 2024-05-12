package aguDataSystem.server.repository

import aguDataSystem.server.repository.RepositoryUtils.DUMMY_DNO_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyTank
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.tank.JDBITankRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
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
		assert(tankByNumber == tank)
	}

	@Test
	fun `get tank by number from an empty AGU should return null`() = testWithHandleAndRollback { handle ->
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
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, 1)

		// assert
		assert(tankByNumber == null)
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
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, Int.MAX_VALUE)

		// assert
		assert(tankByNumber == null)
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
		tankRepo.deleteTank(agu.cui, Int.MAX_VALUE)
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
		tankRepo.updateTank(agu.cui, updatedTank)
		val tankByNumber = tankRepo.getTankByNumber(agu.cui, tankNumber)

		// assert
		assertNotNull(tankByNumber)
		assert(tankByNumber == updatedTank)
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
			tankRepo.updateTank(agu.cui, updatedTank)
		}
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
			tankRepo.updateTank(agu.cui, updatedTank)
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