package aguDataSystem.server.repository

import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.RepositoryUtils.DUMMY_DNO_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyTemperatureMeasures
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.provider.JDBIProviderRepository
import aguDataSystem.server.repository.temperature.JDBITemperatureRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBITemperatureRepositoryTest {

	@Test
	fun `add temperature measures to provider`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val temperatureRepository = JDBITemperatureRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val temperatureMeasures = dummyTemperatureMeasures.onEach(::println)

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)

		// act
		temperatureRepository.addTemperatureMeasuresToProvider(agu.cui, providerId, temperatureMeasures)

		val sut = temperatureRepository.getTemperatureMeasures(providerId, temperatureMeasures.size).onEach(::println)

		// assert
		assert(temperatureMeasures.containsAll(sut))
		assertEquals(temperatureMeasures.last(), sut.last())
	}

	@Test
	fun `get prediction temperature measures for the next two days`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val temperatureRepository = JDBITemperatureRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val temperatureMeasures = dummyTemperatureMeasures
		val nDays = 2

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)
		temperatureRepository.addTemperatureMeasuresToProvider(agu.cui, providerId, temperatureMeasures)

		// act
		val sut = temperatureRepository.getPredictionTemperatureMeasures(providerId, nDays)

		// assert
		assert(temperatureMeasures.containsAll(sut))
	}

	@Test
	fun `get temperature measures for a specific day`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val temperatureRepository = JDBITemperatureRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val temperatureMeasures = dummyTemperatureMeasures
		val date = temperatureMeasures.last().predictionFor.toLocalDate()

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)
		temperatureRepository.addTemperatureMeasuresToProvider(agu.cui, providerId, temperatureMeasures)

		// act
		val sut = temperatureRepository.getTemperatureMeasures(providerId, date)

		// assert
		assert(temperatureMeasures.containsAll(sut))
	}

	@Test
	fun `get past temperature readings`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val temperatureRepository = JDBITemperatureRepository(handle)
		val providerRepository = JDBIProviderRepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val temperatureMeasures = dummyTemperatureMeasures
		val nrOfDays = 2

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)
		temperatureRepository.addTemperatureMeasuresToProvider(agu.cui, providerId, temperatureMeasures)

		// act
		val sut = temperatureRepository.getTemperatureMeasures(providerId, nrOfDays)

		// assert
		assert(temperatureMeasures.containsAll(sut))
	}

}