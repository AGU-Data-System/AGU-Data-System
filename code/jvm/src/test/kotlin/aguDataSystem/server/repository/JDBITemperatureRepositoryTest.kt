package aguDataSystem.server.repository

import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyDNO
import aguDataSystem.server.repository.RepositoryUtils.dummyTank
import aguDataSystem.server.repository.RepositoryUtils.dummyTemperatureMeasures
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.provider.JDBIProviderRepository
import aguDataSystem.server.repository.tank.JDBITankRepository
import aguDataSystem.server.repository.temperature.JDBITemperatureRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(SchemaManagementExtension::class)
class JDBITemperatureRepositoryTest {

    @Test
    fun `add temperature measures to provider`() = testWithHandleAndRollback { handle ->
        // arrange
        val dnoRepository = JDBIDNORepository(handle)
        val aguRepository = JDBIAGURepository(handle)
        val tankRepository = JDBITankRepository(handle)
        val temperatureRepository = JDBITemperatureRepository(handle)
        val providerRepository = JDBIProviderRepository(handle)
        val agu = dummyAGU
        val dno = dummyDNO
        val providerId = 1
        val temperatureMeasures = dummyTemperatureMeasures

        val dnoId = dnoRepository.addDNO(dno).id
        aguRepository.addAGU(agu, dnoId)
        tankRepository.addTank(agu.cui, dummyTank)
        providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)

        // act
        temperatureRepository.addTemperatureMeasuresToProvider(providerId, temperatureMeasures)

        val sut = temperatureRepository.getPastTemperatureMeasures(providerId, temperatureMeasures.size)

        // assert
        assert(temperatureMeasures.containsAll(sut))
        assertEquals(temperatureMeasures.first(), sut.first())
        assertEquals(temperatureMeasures.last(), sut.last())
    }

    @Test
    fun `add temperature measures with predictionFor smaller than timestamp should fail`() =
        testWithHandleAndRollback { handle ->
            // arrange
            val dnoRepository = JDBIDNORepository(handle)
            val aguRepository = JDBIAGURepository(handle)
            val temperatureRepository = JDBITemperatureRepository(handle)
            val providerRepository = JDBIProviderRepository(handle)
            val agu = dummyAGU
            val dno = dummyDNO
            val providerId = 1
            val temperatureMeasures = dummyTemperatureMeasures
            val invalidTemperatureMeasures = temperatureMeasures.map {
                it.copy(predictionFor = it.timestamp.minusSeconds(1))
            }

            val dnoId = dnoRepository.addDNO(dno).id
            aguRepository.addAGU(agu, dnoId)
            providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)

            // act & assert
            assertFailsWith<UnableToExecuteStatementException> {
                temperatureRepository.addTemperatureMeasuresToProvider(providerId, invalidTemperatureMeasures)
            }
        }

    @Test
    fun `add temperature measures with invalid provider should fail`() = testWithHandleAndRollback { handle ->
        // arrange
        val dnoRepository = JDBIDNORepository(handle)
        val aguRepository = JDBIAGURepository(handle)
        val temperatureRepository = JDBITemperatureRepository(handle)
        val agu = dummyAGU
        val dno = dummyDNO
        val providerId = 1
        val temperatureMeasures = dummyTemperatureMeasures

        val dnoId = dnoRepository.addDNO(dno).id
        aguRepository.addAGU(agu, dnoId)

        // act & assert
        assertFailsWith<UnableToExecuteStatementException> {
            temperatureRepository.addTemperatureMeasuresToProvider(providerId, temperatureMeasures)
        }
    }

    @Test
    fun `get temperature measures for the next two days`() = testWithHandleAndRollback { handle ->
        // arrange
        val dnoRepository = JDBIDNORepository(handle)
        val aguRepository = JDBIAGURepository(handle)
        val tankRepository = JDBITankRepository(handle)
        val temperatureRepository = JDBITemperatureRepository(handle)
        val providerRepository = JDBIProviderRepository(handle)
        val agu = dummyAGU
        val dno = dummyDNO
        val providerId = 1
        val temperatureMeasures = dummyTemperatureMeasures
        val nDays = 2

        val dnoId = dnoRepository.addDNO(dno).id
        aguRepository.addAGU(agu, dnoId)
        tankRepository.addTank(agu.cui, dummyTank)
        providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)
        temperatureRepository.addTemperatureMeasuresToProvider(providerId, temperatureMeasures)

        // act
        val sut = temperatureRepository.getPastTemperatureMeasures(providerId, nDays)

        // assert
        assert(temperatureMeasures.containsAll(sut))
    }

    @Test
    fun `get temperature measures for the next two days with invalid provider should return empty list`() =
        testWithHandleAndRollback { handle ->
            // arrange
            val dnoRepository = JDBIDNORepository(handle)
            val aguRepository = JDBIAGURepository(handle)
            val temperatureRepository = JDBITemperatureRepository(handle)
            val agu = dummyAGU
            val dno = dummyDNO
            val providerId = 1
            val nDays = 2

            val dnoId = dnoRepository.addDNO(dno).id
            aguRepository.addAGU(agu, dnoId)

            // act
            val sut = temperatureRepository.getPastTemperatureMeasures(providerId, nDays)

            // assert
            assertEquals(emptyList(), sut)
        }

    @Test
    fun `get temperature measures with negative number of days should return empty list`() =
        testWithHandleAndRollback { handle ->
            // arrange
            val dnoRepository = JDBIDNORepository(handle)
            val aguRepository = JDBIAGURepository(handle)
            val tankRepository = JDBITankRepository(handle)
            val providerRepository = JDBIProviderRepository(handle)
            val temperatureRepository = JDBITemperatureRepository(handle)
            val agu = dummyAGU
            val dno = dummyDNO
            val providerId = 1
            val dummyTemperatureMeasures = dummyTemperatureMeasures
            val nDays = -1

            val dnoId = dnoRepository.addDNO(dno).id
            aguRepository.addAGU(agu, dnoId)
            tankRepository.addTank(agu.cui, dummyTank)
            providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)
            temperatureRepository.addTemperatureMeasuresToProvider(providerId, dummyTemperatureMeasures)

            // act
            val sut = temperatureRepository.getPastTemperatureMeasures(providerId, nDays)

            // assert
            assertEquals(emptyList(), sut)
        }

    @Test
    fun `get temperature measures with invalid provider id should return empty list`() =
        testWithHandleAndRollback { handle ->
            // arrange
            val temperatureRepository = JDBITemperatureRepository(handle)
            val providerId = 1
            val nDays = 2

            // act
            val sut = temperatureRepository.getPastTemperatureMeasures(providerId, nDays)

            // assert
            assertEquals(emptyList(), sut)
        }

    @Test
    fun `get prediction temperature measures for the next two days`() = testWithHandleAndRollback { handle ->
        // arrange
        val dnoRepository = JDBIDNORepository(handle)
        val aguRepository = JDBIAGURepository(handle)
        val tankRepository = JDBITankRepository(handle)
        val temperatureRepository = JDBITemperatureRepository(handle)
        val providerRepository = JDBIProviderRepository(handle)
        val agu = dummyAGU
        val dno = dummyDNO
        val providerId = 1
        val temperatureMeasures = dummyTemperatureMeasures
        val nDays = 2

        val dnoId = dnoRepository.addDNO(dno).id
        aguRepository.addAGU(agu, dnoId)
        tankRepository.addTank(agu.cui, dummyTank)
        providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)
        temperatureRepository.addTemperatureMeasuresToProvider(providerId, temperatureMeasures)

        // act
        val sut = temperatureRepository.getPredictionTemperatureMeasures(providerId, nDays)

        // assert
        assert(temperatureMeasures.containsAll(sut))
    }

    @Test
    fun `get temperature prediction measures with negative number of days should return empty list`() =
        testWithHandleAndRollback { handle ->
            // arrange
            val dnoRepository = JDBIDNORepository(handle)
            val aguRepository = JDBIAGURepository(handle)
            val tankRepository = JDBITankRepository(handle)
            val providerRepository = JDBIProviderRepository(handle)
            val temperatureRepository = JDBITemperatureRepository(handle)
            val agu = dummyAGU
            val dno = dummyDNO
            val providerId = 1
            val dummyTemperatureMeasures = dummyTemperatureMeasures
            val nDays = -1

            val dnoId = dnoRepository.addDNO(dno).id
            aguRepository.addAGU(agu, dnoId)
            tankRepository.addTank(agu.cui, dummyTank)
            providerRepository.addProvider(agu.cui, 1, ProviderType.TEMPERATURE)
            temperatureRepository.addTemperatureMeasuresToProvider(providerId, dummyTemperatureMeasures)

            // act
            val sut = temperatureRepository.getPredictionTemperatureMeasures(providerId, nDays)

            // assert
            assertEquals(emptyList(), sut)
        }

    @Test
    fun `get past temperature readings`() = testWithHandleAndRollback { handle ->
        // arrange
        val dnoRepository = JDBIDNORepository(handle)
        val aguRepository = JDBIAGURepository(handle)
        val tankRepository = JDBITankRepository(handle)
        val temperatureRepository = JDBITemperatureRepository(handle)
        val providerRepository = JDBIProviderRepository(handle)
        val agu = dummyAGU
        val dno = dummyDNO
        val providerId = 1
        val temperatureMeasures = dummyTemperatureMeasures
        val nrOfDays = 2

        val dnoId = dnoRepository.addDNO(dno).id
        aguRepository.addAGU(agu, dnoId)
        tankRepository.addTank(agu.cui, dummyTank)
        providerRepository.addProvider(agu.cui, providerId, ProviderType.TEMPERATURE)
        temperatureRepository.addTemperatureMeasuresToProvider(providerId, temperatureMeasures)

        // act
        val sut = temperatureRepository.getPastTemperatureMeasures(providerId, nrOfDays)

        // assert
        assert(temperatureMeasures.containsAll(sut))
    }

    @Test
    fun `get past temperature readings with invalid provider id should return empty list`() =
        testWithHandleAndRollback { handle ->
            // arrange
            val temperatureRepository = JDBITemperatureRepository(handle)
            val providerId = 1
            val nrOfDays = 2

            // act
            val sut = temperatureRepository.getPastTemperatureMeasures(providerId, nrOfDays)

            // assert
            assertEquals(emptyList(), sut)
        }
}
