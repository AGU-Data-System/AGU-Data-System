package aguDataSystem.server.service

import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.service.ServiceUtils.dummyAGUCreationDTO
import aguDataSystem.server.service.ServiceUtils.dummyDNODTO
import aguDataSystem.server.service.ServiceUtils.dummyGasLevels
import aguDataSystem.server.service.ServiceUtils.dummyGasLevelsDTO
import aguDataSystem.server.service.ServiceUtils.dummyTank
import aguDataSystem.server.service.ServiceUtils.updateTankDTO
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.chron.ChronService
import aguDataSystem.server.service.chron.FetchService
import aguDataSystem.server.service.dno.DNOService
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.server.service.errors.agu.update.UpdateFavouriteStateError
import aguDataSystem.server.service.errors.agu.update.UpdateGasLevelsError
import aguDataSystem.server.service.errors.agu.update.UpdateNotesError
import aguDataSystem.server.service.errors.contact.AddContactError
import aguDataSystem.server.service.errors.contact.DeleteContactError
import aguDataSystem.server.service.errors.measure.GetMeasuresError
import aguDataSystem.server.service.errors.tank.AddTankError
import aguDataSystem.server.service.errors.tank.UpdateTankError
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import aguDataSystem.utils.getFailureOrThrow
import aguDataSystem.utils.getSuccessOrThrow
import aguDataSystem.utils.isFailure
import aguDataSystem.utils.isSuccess
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertContains
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class AGUServiceTest {

	private val aguDomain = AGUDomain()

	@Test
	fun `create AGU with valid data`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isSuccess())
	}

	@Test
	fun `create AGU with invalid tank`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf())

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidTank)
	}

	@Test
	fun `create AGU without any DNO`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.DNONotFound)
	}

	@Test
	fun `create AGU with invalid DNO`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(dnoName = dummyDNODTO.copy(name = "invalid").name))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.DNONotFound)
	}

	@Test
	fun `create AGU with invalid coordinates`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(location = creationAgu.location.copy(latitude = 91.0)))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidCoordinates)
	}

	@Test
	fun `create AGU with invalid cui`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(cui = "invalid"))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidCUI)
	}

	@Test
	fun `create AGU with invalid name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(name = ""))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidName)
	}

	@Test
	fun `create AGU with invalid min level`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(levels = creationAgu.levels.copy(min = -1)))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidMinLevel)
	}

	@Test
	fun `create AGU with invalid max level`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(levels = creationAgu.levels.copy(max = -1)))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidMaxLevel)
	}

	@Test
	fun `create AGU with invalid critical level`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(levels = creationAgu.levels.copy(critical = -1)))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidCriticalLevel)
	}

	@Test
	fun `create AGU with invalid levels`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(levels = creationAgu.levels.copy(min = 100)))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidLevels)
	}

	@Test
	fun `create AGU with invalid load volume`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(loadVolume = -1))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidLoadVolume)
	}

	@Test
	fun `create AGU with invalid contact type`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(
			tanks = listOf(dummyTank),
			contacts = listOf(ServiceUtils.dummyLogisticContact.copy(type = "invalid"))
		)

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidContactType)
	}

	@Test
	fun `create AGU with invalid contact name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(
			tanks = listOf(dummyTank),
			contacts = listOf(ServiceUtils.dummyLogisticContact.copy(name = ""))
		)

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidContact)
	}

	@Test
	fun `create AGU with invalid contact phone number`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(
			tanks = listOf(dummyTank),
			contacts = listOf(ServiceUtils.dummyLogisticContact.copy(phone = ""))
		)

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidContact)
	}

	@Test
	fun `create AGU twice should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		aguService.createAGU(creationAgu)

		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.AGUAlreadyExists)
	}

	@Test
	fun `create AGU with the same name twice should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu1 = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val creationAgu2 =
				dummyAGUCreationDTO.copy(cui = "PT1234567890123456XX", tanks = listOf(dummyTank))

			dnoService.createDNO(dnoCreation)
			aguService.createAGU(creationAgu1)

			// act
			val result = aguService.createAGU(creationAgu2)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.AGUNameAlreadyExists)
		}

	//needs test for provider error
	// good luck guys
	// TODO

	@Test
	fun `get agu by its id`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.getAGUById(agu.getSuccessOrThrow())

		// assert
		assert(result.isSuccess())
		assert(result.getSuccessOrThrow().name == creationAgu.name)
	}

	@Test
	fun `get agu by its id with invalid id`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.getAGUById("invalid")

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is GetAGUError.InvalidCUI)
	}

	@Test
	fun `get temperature measures by agu id and days`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val days = 2

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.getTemperatureMeasures(agu.getSuccessOrThrow(), days)

		// assert
		assert(result.isSuccess())
	}

	@Test
	fun `get temperature measures by agu id and days with invalid id`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val days = 2

			// act
			val result = aguService.getTemperatureMeasures("invalid", days)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidCUI)
		}

	@Test
	fun `get temperature measures by agu id and days with invalid days`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val days = -1

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.getTemperatureMeasures(agu.getSuccessOrThrow(), days)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidDays)
		}

	// TODO Needs test for provider error

	@Test
	fun `get daily gas measures at a certain hour for several days correctly`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val hour = LocalTime.of(12, 0)
			val days = 2

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.getDailyGasMeasures(agu.getSuccessOrThrow(), days, hour)

			// assert
			assert(result.isSuccess())
		}

	@Test
	fun `get daily gas measures at a certain hour for several days with invalid id`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val hour = LocalTime.of(12, 0)
			val days = 2

			// act
			val result = aguService.getDailyGasMeasures("invalid", days, hour)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidCUI)
		}

	@Test
	fun `get daily gas measures at a certain hour for several days with invalid days`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val hour = LocalTime.of(12, 0)
			val days = -1

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.getDailyGasMeasures(agu.getSuccessOrThrow(), days, hour)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidDays)
		}

	@Test
	fun `get daily gas measures at a certain hour for several days with invalid hour`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val hour = LocalTime.of(25, 0) // TODO Check test fails here due to invalid hour 0 - 23
			val days = 2

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.getDailyGasMeasures(agu.getSuccessOrThrow(), days, hour)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidTime)
		}

	// TODO Needs test for provider error

	@Test
	fun `get hourly gas measures at a certain day for several hours correctly`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val day = LocalDate.now()

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.getHourlyGasMeasures(agu.getSuccessOrThrow(), day)

			// assert
			assert(result.isSuccess())
			// will be an empty list
		}

	@Test
	fun `get hourly gas measures at a certain day for several hours with invalid id`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val day = LocalDate.now()

			// act
			val result = aguService.getHourlyGasMeasures("invalid", day)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidCUI)
		}

	@Test
	fun `get prediction gas measures for several days at a certain hour correctly`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val days = 2
			val time = LocalTime.of(12, 0)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.getPredictionGasLevels(agu.getSuccessOrThrow(), days, time)

			// assert
			assert(result.isSuccess())
		}

	@Test
	fun `get prediction gas measures for several days at a certain hour with invalid id`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val days = 2
			val time = LocalTime.of(12, 0)

			// act
			val result = aguService.getPredictionGasLevels("invalid", days, time)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidCUI)
		}

	@Test
	fun `get prediction gas measures for several days at a certain hour with invalid days`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val days = -1
			val time = LocalTime.of(12, 0)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.getPredictionGasLevels(agu.getSuccessOrThrow(), days, time)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidDays)
		}

	@Test
	fun `get prediction gas measures for several days at a certain hour with invalid hour`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val days = 2
			val time = LocalTime.of(25, 0) // TODO Check test fails here due to invalid hour 0 - 23

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.getPredictionGasLevels(agu.getSuccessOrThrow(), days, time)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidTime)
		}

	// TODO Needs test for provider error

	@Test
	fun `update agu's favorite state correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.updateFavouriteState(agu.getSuccessOrThrow(), true)

		// assert
		assert(result.isSuccess())
		assert(result.getSuccessOrThrow().isFavorite)
	}

	@Test
	fun `update agu's favorite state with invalid id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateFavouriteState("invalid", true)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateFavouriteStateError.AGUNotFound)
		}

	@Test
	fun `add contact to AGU correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val contact = ServiceUtils.dummyLogisticContact

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.addContact(agu.getSuccessOrThrow(), contact)
		val updatedAGU = aguService.getAGUById(agu.getSuccessOrThrow()).getSuccessOrThrow()

		// assert
		assert(result.isSuccess())
		assertContains(updatedAGU.contacts.map { it.id }, result.getSuccessOrThrow())
	}

	@Test
	fun `add contact to AGU with invalid id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val contact = ServiceUtils.dummyLogisticContact

			// act
			val result = aguService.addContact("invalid", contact)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddContactError.AGUNotFound)
		}

	@Test
	fun `add contact to AGU with invalid contact type should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val contact = ServiceUtils.dummyLogisticContact.copy(type = "invalid")

			// act
			val result = aguService.addContact("invalid", contact)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddContactError.InvalidContactType)
		}

	@Test
	fun `add contact to AGU with invalid contact name should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val contact = ServiceUtils.dummyLogisticContact.copy(name = "")

			// act
			val result = aguService.addContact("invalid", contact)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddContactError.InvalidContact)
		}

	@Test
	fun `add contact to AGU with invalid contact phone number should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val contact = ServiceUtils.dummyLogisticContact.copy(phone = "")

			// act
			val result = aguService.addContact("invalid", contact)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddContactError.InvalidContact)
		}

	@Test
	fun `add same contact twice should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val contact = ServiceUtils.dummyLogisticContact

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
		aguService.addContact(agu.getSuccessOrThrow(), contact)

		// act
		val result = aguService.addContact(agu.getSuccessOrThrow(), contact)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AddContactError.ContactAlreadyExists)
	}

	@Test
	fun `remove contact from AGU correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val contact = ServiceUtils.dummyLogisticContact

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
		val addedContact = aguService.addContact(agu.getSuccessOrThrow(), contact).getSuccessOrThrow()

		// act
		val result = aguService.deleteContact(agu.getSuccessOrThrow(), addedContact)
		val updatedAGU = aguService.getAGUById(agu.getSuccessOrThrow()).getSuccessOrThrow()

		// assert
		assert(result.isSuccess())
		assert(!updatedAGU.contacts.map { it.id }.contains(addedContact))
	}

	@Test
	fun `remove contact from AGU with invalid id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val contactId = 1

			// act
			val result = aguService.deleteContact("invalid", contactId)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is DeleteContactError.AGUNotFound)
		}

	@Test
	fun `remove contact from AGU with invalid contact id does nothing`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val contactId = -1

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.deleteContact(agu.getSuccessOrThrow(), contactId)

			// assert
			assert(result.isSuccess())
		}

	@Test
	fun `add tank to agu correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val tank = dummyTank.copy(number = 2)

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.addTank(agu.getSuccessOrThrow(), tank)
		val updatedAGU = aguService.getAGUById(agu.getSuccessOrThrow()).getSuccessOrThrow()

		// assert
		assert(result.isSuccess())
		assertContains(updatedAGU.tanks.map { it.number }, result.getSuccessOrThrow())
	}

	@Test
	fun `add tank to agu with invalid id should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val tank = dummyTank

		// act
		val result = aguService.addTank("invalid", tank)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AddTankError.AGUNotFound)
	}

	@Test
	fun `add tank to agu with invalid tank number should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val tank = dummyTank.copy(number = -1)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddTankError.InvalidTankNumber)
		}

	@Test
	fun `add tank to agu with invalid levels should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
			val tank = dummyTank.copy(levels = dummyGasLevels.copy(min = 100))

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddTankError.InvalidLevels)
		}

	@Test
	fun `add tank twice should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val tank = dummyTank

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
		aguService.addTank(agu.getSuccessOrThrow(), tank)

		// act
		val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AddTankError.TankAlreadyExists)
	}

	@Test
	fun `update tank correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val tank = dummyTank.copy(number = 2)

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
		val addedTank = aguService.addTank(agu.getSuccessOrThrow(), tank).getSuccessOrThrow()

		// act
		val result = aguService.updateTank(agu.getSuccessOrThrow(), addedTank, updateTankDTO)
		val updatedAGU = aguService.getAGUById(agu.getSuccessOrThrow()).getSuccessOrThrow()

		// assert
		assert(result.isSuccess())
		assert(updatedAGU.tanks.first { it.number == addedTank }.levels.min == updateTankDTO.minLevel)
	}

	@Test
	fun `update tank with invalid id should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		// act
		val result = aguService.updateTank("invalid", 1, updateTankDTO)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is UpdateTankError.InvalidCUI)
	}

	@Test
	fun `update tank with invalid tank number should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateTank(agu.getSuccessOrThrow(), -1, updateTankDTO)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidTankNumber)
		}

	@Test
	fun `update tank with invalid levels should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val tank = dummyTank.copy(number = 2)

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
		val addedTank = aguService.addTank(agu.getSuccessOrThrow(), tank).getSuccessOrThrow()

		// act
		val result = aguService.updateTank(agu.getSuccessOrThrow(), addedTank, updateTankDTO.copy(minLevel = 100))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is UpdateTankError.InvalidLevels)
	}

	@Test
	fun `update AGU Gas Levels correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoService = DNOService(transactionManager)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.updateGasLevels(agu.getSuccessOrThrow(), dummyGasLevelsDTO)

		// assert
		assert(result.isSuccess())
		assert(result.getSuccessOrThrow().levels.min == dummyGasLevelsDTO.min)
		assert(result.getSuccessOrThrow().levels.max == dummyGasLevelsDTO.max)
		assert(result.getSuccessOrThrow().levels.critical == dummyGasLevelsDTO.critical)
	}

	@Test
	fun `update AGU Gas Levels with invalid AGU ID should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateGasLevels("invalid", dummyGasLevelsDTO)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateGasLevelsError.AGUNotFound)
		}

	@Test
	fun `update AGU Gas Levels with invalid Gas Levels should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoService = DNOService(transactionManager)

			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateGasLevels(agu.getSuccessOrThrow(), dummyGasLevelsDTO.copy(min = 100))

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateGasLevelsError.InvalidLevels)
		}

	@Test
	fun `update AGU notes correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoService = DNOService(transactionManager)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.updateNotes(agu.getSuccessOrThrow(), "new notes")

		// assert
		assert(result.isSuccess())
		assert(result.getSuccessOrThrow().notes == "new notes")
	}

	@Test
	fun `update AGU notes with invalid AGU ID should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateNotes("invalid", "new notes")

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateNotesError.AGUNotFound)
		}

	@Test
	fun `update AGU notes with empty notes should update`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoService = DNOService(transactionManager)

			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateNotes(agu.getSuccessOrThrow(), "")

			// assert
			assert(result.isSuccess())
			assert(result.getSuccessOrThrow().notes == "")
		}
}