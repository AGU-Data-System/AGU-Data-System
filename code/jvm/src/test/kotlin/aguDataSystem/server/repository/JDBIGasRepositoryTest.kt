package aguDataSystem.server.repository

import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyDNO
import aguDataSystem.server.repository.RepositoryUtils.dummyGasMeasures
import aguDataSystem.server.repository.RepositoryUtils.dummyTank
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.gas.JDBIGasRepository
import aguDataSystem.server.repository.provider.JDBIProviderRepository
import aguDataSystem.server.repository.tank.JDBITankRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIGasRepositoryTest {

	@Test
	fun `add gas measures to provider`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val tankRepository = JDBITankRepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dno = dummyDNO
		val tank = dummyTank
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val day = gasMeasures.first().timestamp.toLocalDate()

		val dnoId = dnoRepository.addDNO(dno).id
		aguRepository.addAGU(agu, dnoId)
		tankRepository.addTank(agu.cui, tank)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)

		// act
		gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)

		val sut = gasRepository.getGasMeasures(providerId, day)
		// TODO check this

		// assert
		assert(gasMeasures.containsAll(sut))
		assertEquals(gasMeasures.first(), sut.first())
		assertEquals(gasMeasures.last(), sut.last())
	}

	@Test
	fun `add gas measures with predictionFor smaller than timestamp should fail`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val tankRepository = JDBITankRepository(handle)
			val gasRepository = JDBIGasRepository(handle)
			val providerRepository = JDBIProviderRepository(handle)
			val agu = dummyAGU
			val dno = dummyDNO
			val tank = dummyTank
			val providerId = 1
			val gasMeasures = dummyGasMeasures.map {
				it.copy(
					predictionFor = it.timestamp,
					timestamp = it.timestamp.minusMinutes(10)
				)
			}

			val dnoId = dnoRepository.addDNO(dno).id
			aguRepository.addAGU(agu, dnoId)
			tankRepository.addTank(agu.cui, tank)
			providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)

			// act & assert
			assertFailsWith<UnableToExecuteStatementException> {
				gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)
			}
		}

	@Test
	fun `add gas measures with invalid provider should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val tankRepository = JDBITankRepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val agu = dummyAGU
		val dno = dummyDNO
		val tank = dummyTank
		val providerId = 1
		val gasMeasures = dummyGasMeasures

		val dnoId = dnoRepository.addDNO(dno).id
		aguRepository.addAGU(agu, dnoId)
		tankRepository.addTank(agu.cui, tank)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)
		}
	}

	@Test
	fun `add gas measures with invalid tank number should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val tankRepository = JDBITankRepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dno = dummyDNO
		val tank = dummyTank
		val providerId = 1
		val gasMeasures = dummyGasMeasures.map { it.copy(tankNumber = 2) }

		val dnoId = dnoRepository.addDNO(dno).id
		aguRepository.addAGU(agu, dnoId)
		tankRepository.addTank(agu.cui, tank)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)
		}
	}

	@Test
	fun `get gas measures for the next two days`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val tankRepository = JDBITankRepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dno = dummyDNO
		val tank = dummyTank
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val nDays = 2
		val time = gasMeasures.last().predictionFor?.toLocalTime()
		requireNotNull(time)

		val dnoId = dnoRepository.addDNO(dno).id
		aguRepository.addAGU(agu, dnoId)
		tankRepository.addTank(agu.cui, tank)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
		gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)

		// act
		val sut = gasRepository.getGasMeasures(providerId, nDays, time)

		// assert
		assert(sut.maxOf { it.timestamp } <= gasMeasures.last().timestamp)
		assert(gasMeasures.containsAll(sut))
	}

	@Test
	fun `get gas measures with negative number of days should return empty list`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val tankRepository = JDBITankRepository(handle)
			val gasRepository = JDBIGasRepository(handle)
			val providerRepository = JDBIProviderRepository(handle)
			val agu = dummyAGU
			val dno = dummyDNO
			val tank = dummyTank
			val providerId = 1
			val gasMeasures = dummyGasMeasures
			val nDays = Int.MIN_VALUE
			val time = gasMeasures.last().predictionFor?.toLocalTime()
			requireNotNull(time)

			val dnoId = dnoRepository.addDNO(dno).id
			aguRepository.addAGU(agu, dnoId)
			tankRepository.addTank(agu.cui, tank)
			providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
			gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)

			// act
			val sut = gasRepository.getGasMeasures(providerId, nDays, time)

			// assert
			assertEquals(0, sut.size)
		}

	@Test
	fun `get gas measures with invalid provider id should return empty list`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val tankRepository = JDBITankRepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dno = dummyDNO
		val tank = dummyTank
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val nDays = 2
		val time = gasMeasures.last().predictionFor?.toLocalTime()
		requireNotNull(time)

		val dnoId = dnoRepository.addDNO(dno).id
		aguRepository.addAGU(agu, dnoId)
		tankRepository.addTank(agu.cui, tank)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
		gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)

		// act
		val sut = gasRepository.getGasMeasures(Int.MIN_VALUE, nDays, time)

		// assert
		assertEquals(0, sut.size)
	}

	@Test
	fun `get gas measures for a past day`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val tankRepository = JDBITankRepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dno = dummyDNO
		val tank = dummyTank
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val day = gasMeasures.first().timestamp.toLocalDate()

		val dnoId = dnoRepository.addDNO(dno).id
		aguRepository.addAGU(agu, dnoId)
		tankRepository.addTank(agu.cui, tank)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
		gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)

		// act
		val sut = gasRepository.getGasMeasures(providerId, day)

		// assert
		assertContains(sut, gasMeasures.first())
		assertEquals(1, sut.size)
	}

	@Test
	fun `get gas measures for a future day should return empty list`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val tankRepository = JDBITankRepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dno = dummyDNO
		val tank = dummyTank
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val day = gasMeasures.last().timestamp.toLocalDate().plusDays(1)

		val dnoId = dnoRepository.addDNO(dno).id
		aguRepository.addAGU(agu, dnoId)
		tankRepository.addTank(agu.cui, tank)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
		gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)

		// act
		val sut = gasRepository.getGasMeasures(providerId, day)

		// assert
		assertEquals(0, sut.size)
	}

	@Test
	fun `get gas measure with a invalid provider id should return empty list`() = testWithHandleAndRollback { handle ->
		// arrange
		val gasRepository = JDBIGasRepository(handle)
		val providerId = Int.MIN_VALUE
		val day = dummyGasMeasures.first().timestamp.toLocalDate()

		// act
		val sut = gasRepository.getGasMeasures(providerId, day)

		// assert
		assertEquals(0, sut.size)
	}

	@Test
	fun `get gas prediction measures for the next two days`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val tankRepository = JDBITankRepository(handle)
			val gasRepository = JDBIGasRepository(handle)
			val providerRepository = JDBIProviderRepository(handle)
			val agu = dummyAGU
			val dno = dummyDNO
			val tank = dummyTank
			val providerId = 1
			val gasMeasures = dummyGasMeasures
			val nDays = 2
			val time = gasMeasures.last().predictionFor?.plusDays(1)?.toLocalTime()
			requireNotNull(time)

			val dnoId = dnoRepository.addDNO(dno).id
			aguRepository.addAGU(agu, dnoId)
			tankRepository.addTank(agu.cui, tank)
			providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
			gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)

			// act
			val sut = gasRepository.getPredictionGasMeasures(providerId, nDays, time)

			// assert
			assertEquals(nDays, sut.size)
			assertEquals(
				sut,
				gasMeasures.drop(1).take(sut.size)
			) // drop the first element because it is the today's measure
		}

	@Test
	fun `get gas prediction measures with negative number of days should return empty list`() =
		testWithHandleAndRollback { handle ->
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val tankRepository = JDBITankRepository(handle)
			val gasRepository = JDBIGasRepository(handle)
			val providerRepository = JDBIProviderRepository(handle)
			val agu = dummyAGU
			val dno = dummyDNO
			val tank = dummyTank
			val providerId = 1
			val gasMeasures = dummyGasMeasures
			val nDays = Int.MIN_VALUE
			val time = gasMeasures.last().predictionFor?.plusDays(1)?.toLocalTime()
			requireNotNull(time)

			val dnoId = dnoRepository.addDNO(dno).id
			aguRepository.addAGU(agu, dnoId)
			tankRepository.addTank(agu.cui, tank)
			providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
			gasRepository.addGasMeasuresToProvider(providerId, gasMeasures)

			// act
			val sut = gasRepository.getPredictionGasMeasures(providerId, nDays, time)

			// assert
			assertEquals(0, sut.size)
		}

	@Test
	fun `get gas prediction measures with invalid provider id should return empty list`() =
		testWithHandleAndRollback { handle ->
			val gasRepository = JDBIGasRepository(handle)
			val providerId = Int.MIN_VALUE
			val nDays = 2
			val time = dummyGasMeasures.last().predictionFor?.plusDays(1)?.toLocalTime()
			requireNotNull(time)

			// act
			val sut = gasRepository.getPredictionGasMeasures(providerId, nDays, time)

			// assert
			assertEquals(0, sut.size)
		}
}
