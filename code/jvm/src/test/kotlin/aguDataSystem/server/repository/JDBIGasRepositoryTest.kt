package aguDataSystem.server.repository

import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.RepositoryUtils.DUMMY_DNO_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyGasMeasures
import aguDataSystem.server.repository.RepositoryUtils.dummyTank
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.gas.JDBIGasRepository
import aguDataSystem.server.repository.provider.JDBIProviderRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIGasRepositoryTest {

	@Test
	fun `add gas measures to provider`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU.copy(tanks = listOf(dummyTank))
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val time = gasMeasures.last().predictionFor.toLocalTime()

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)

		// act
		gasRepository.addGasMeasuresToProvider(agu.cui, providerId, gasMeasures)

		val sut = gasRepository.getGasMeasures(providerId, gasMeasures.size, time)

		// assert
		assert(gasMeasures.containsAll(sut))
		assertEquals(gasMeasures.last(), sut.last())
	}

	@Test
	fun `get gas measures for the next two days`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU.copy(tanks = listOf(dummyTank))
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val nDays = 2
		val time = gasMeasures.last().predictionFor.toLocalTime()

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
		gasRepository.addGasMeasuresToProvider(agu.cui, providerId, gasMeasures)

		// act
		val sut = gasRepository.getGasMeasures(providerId, nDays, time)

		// assert
		assert(sut.maxOf { it.timestamp } <= gasMeasures.last().timestamp)
		assert(gasMeasures.containsAll(sut))
	}

	@Test
	fun `get gas measures for a specific day`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU.copy(tanks = listOf(dummyTank))
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val day = gasMeasures.first().timestamp.toLocalDate()

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
		gasRepository.addGasMeasuresToProvider(agu.cui, providerId, gasMeasures)

		// act
		val sut = gasRepository.getGasMeasures(providerId, day)

		// assert
		assertContains(sut, gasMeasures.last())
		assertEquals(1, sut.size)
	}

	@Test
	fun `get gas measures for the next days based on tomorrow's date`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val gasRepository = JDBIGasRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU.copy(tanks = listOf(dummyTank))
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val gasMeasures = dummyGasMeasures
		val nDays = 2
		val time = gasMeasures.last().predictionFor.plusDays(1).toLocalTime()

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.GAS)
		gasRepository.addGasMeasuresToProvider(agu.cui, providerId, gasMeasures)

		// act
		val sut = gasRepository.getPredictionGasMeasures(providerId, nDays, time)

		// assert
		assertEquals(nDays, sut.size)
		assertEquals(sut, gasMeasures.take(sut.size))
	}
}