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
import aguDataSystem.server.service.errors.agu.update.UpdateActiveStateError
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
import kotlin.test.assertFalse
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
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isSuccess())
	}

	@Test
	fun `create AGU with lower boundary latitude values`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO

			dnoService.createDNO(dnoCreation)

			// act
			val creationAgu = dummyAGUCreationDTO.copy(location = dummyAGUCreationDTO.location.copy(latitude = -90.0))
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isSuccess())
		}

	@Test
	fun `create AGU with upper boundary latitude values`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO

			dnoService.createDNO(dnoCreation)
			val creationAgu = dummyAGUCreationDTO.copy(location = dummyAGUCreationDTO.location.copy(latitude = 90.0))

			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isSuccess())
		}

	@Test
	fun `create AGU with upper boundary longitude values`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO

			dnoService.createDNO(dnoCreation)
			val creationAgu = dummyAGUCreationDTO.copy(location = dummyAGUCreationDTO.location.copy(longitude = 180.0))

			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isSuccess())
		}

	@Test
	fun `create AGU with lower boundary longitude values`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO

			dnoService.createDNO(dnoCreation)
			val creationAgu = dummyAGUCreationDTO.copy(location = dummyAGUCreationDTO.location.copy(longitude = -180.0))

			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isSuccess())
		}

	@Test
	fun `create AGU twice should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		aguService.createAGU(creationAgu)

		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.AGUAlreadyExists)
	}

	@Test
	fun `create AGU with invalid cui`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(cui = "invalid"))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidCUI)
	}


	@Test
	fun `create AGU with empty cui`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(cui = ""))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidCUI)
	}

	@Test
	fun `create AGU with already used cui should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu1 = dummyAGUCreationDTO
		val creationAgu2 =
			dummyAGUCreationDTO.copy(eic = "another eic", name = "Another name", tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		aguService.createAGU(creationAgu1)

		// act
		val result = aguService.createAGU(creationAgu2)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.AGUAlreadyExists)
	}

	@Test
	fun `create AGU with empty eic should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(eic = "", tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidEIC)
	}

	@Test
	fun `create AGU with already used EIC should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu1 = dummyAGUCreationDTO
		val creationAgu2 =
			dummyAGUCreationDTO.copy(cui = "PT6543210987654321XX", name = "Another name", tanks = listOf(dummyTank))

		dnoService.createDNO(dnoCreation)
		aguService.createAGU(creationAgu1)

		// act
		val result = aguService.createAGU(creationAgu2)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.AGUAlreadyExists)
	}

	@Test
	fun `create AGU with invalid name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(name = ""))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidName)
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
			val creationAgu1 = dummyAGUCreationDTO
			val creationAgu2 =
				dummyAGUCreationDTO.copy(cui = "PT1234567890123456XX", tanks = listOf(dummyTank))

			dnoService.createDNO(dnoCreation)
			aguService.createAGU(creationAgu1)

			// act
			val result = aguService.createAGU(creationAgu2)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.AGUAlreadyExists)
		}

	@Test
	fun `create AGU with invalid min level`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

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
		val creationAgu = dummyAGUCreationDTO

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
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(levels = creationAgu.levels.copy(critical = -1)))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidCriticalLevel)
	}

	@Test
	fun `create AGU with critical over under min level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(
				levels = dummyGasLevels.copy(min = dummyGasLevels.critical - 1)
			)

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidLevels)
		}

	@Test
	fun `create AGU with critical level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(
				levels = dummyGasLevels.copy(critical = dummyGasLevels.max + 1)
			)

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidLevels)
		}

	@Test
	fun `create AGU with min level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(
				tanks = listOf(dummyTank),
				levels = dummyGasLevels.copy(min = dummyGasLevels.max + 1)
			)

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidLevels)
		}

	@Test
	fun `create AGU with invalid latitude`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(location = creationAgu.location.copy(latitude = 91.0)))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidCoordinates)
	}

	@Test
	fun `create AGU with invalid longitude`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(
			tanks = listOf(dummyTank),
			location = dummyAGUCreationDTO.location.copy(longitude = 181.0)
		)

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidCoordinates)
	}

	@Test
	fun `create AGU without any DNO`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val creationAgu = dummyAGUCreationDTO

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
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(dnoName = dummyDNODTO.copy(name = "").name))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.DNONotFound)
	}

	@Test
	fun `create AGU with un existing DNO`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val creationAgu = dummyAGUCreationDTO

		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.DNONotFound)
	}

	@Test
	fun `create AGU with invalid gas url`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val creationAgu = dummyAGUCreationDTO

		// act
		val result = aguService.createAGU(creationAgu.copy(gasLevelUrl = "invalid"))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.ProviderError)
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
	fun `create AGU without any tank`() = testWithTransactionManagerAndRollback { transactionManager ->
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
	fun `create AGU with invalid tank number`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val invalidTank = dummyTank.copy(number = -1)
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(invalidTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidTank)
	}

	@Test
	fun `create AGU with invalid tank capacity volume`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val invalidTank = dummyTank.copy(capacity = -1)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(invalidTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidTank)
	}

	@Test
	fun `create AGU with tank critical level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val invalidTank = dummyTank.copy(levels = dummyGasLevels.copy(critical = dummyGasLevels.max + 1))
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(invalidTank))

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidLevels)
		}

	@Test
	fun `create AGU with tank critical level under min level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val invalidTank = dummyTank.copy(levels = dummyGasLevels.copy(min = dummyGasLevels.critical - 1))
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(invalidTank))

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidLevels)
		}

	@Test
	fun `create AGU with negative tank min level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val invalidTank = dummyTank.copy(levels = dummyGasLevels.copy(min = -1))
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(invalidTank))

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidMinLevel)
		}

	@Test
	fun `create AGU with negative tank max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val invalidTank = dummyTank.copy(levels = dummyGasLevels.copy(max = -1))
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(invalidTank))

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidMaxLevel)
		}

	@Test
	fun `create AGU with negative tank critical level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val invalidTank = dummyTank.copy(levels = dummyGasLevels.copy(critical = -1))
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(invalidTank))

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidCriticalLevel)
		}

	@Test
	fun `create AGU with tank min level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val invalidTank = dummyTank.copy(levels = dummyGasLevels.copy(min = dummyGasLevels.max + 1))
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(invalidTank))

			dnoService.createDNO(dnoCreation)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.InvalidLevels)
		}

	@Test
	fun `create agu with un existing transport companies`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val creationAgu =
				dummyAGUCreationDTO.copy(tanks = listOf(dummyTank), transportCompanies = listOf("un existing"))

			dnoService.createDNO(dummyDNODTO)
			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.TransportCompanyNotFound)
		}

	@Test
	fun `create AGU without transport companies name`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoService = DNOService(transactionManager)
			dnoService.createDNO(dummyDNODTO)
			val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank), transportCompanies = listOf(""))

			// act
			val result = aguService.createAGU(creationAgu)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AGUCreationError.TransportCompanyNotFound)
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
		val creationAgu = dummyAGUCreationDTO

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
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.getAGUById("invalid")

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is GetAGUError.InvalidCUI)
	}

	@Test
	fun `get agu by its id with empty id`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		// act
		val result = aguService.getAGUById("")

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
		val creationAgu = dummyAGUCreationDTO
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
	fun `get temperature measures by id and days with empty id`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val days = 2

			// act
			val result = aguService.getTemperatureMeasures("", days)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidCUI)
		}

	@Test
	fun `get temperature measures by id with un existing agu`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val days = 2

			// act
			val result = aguService.getTemperatureMeasures("PT6543210987654321XX", days)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.AGUNotFound)
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
			val creationAgu = dummyAGUCreationDTO
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
			val creationAgu = dummyAGUCreationDTO
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
	fun `get daily gas measures at a certain hour for several days with empty id`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val hour = LocalTime.of(12, 0)
			val days = 2

			// act
			val result = aguService.getDailyGasMeasures("", days, hour)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidCUI)
		}

	@Test
	fun `get daily gas measures at a certain hour for several days with un existing agu`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val hour = LocalTime.of(12, 0)
			val days = 2

			// act
			val result = aguService.getDailyGasMeasures("PT6543210987654321XX", days, hour)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.AGUNotFound)
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
			val creationAgu = dummyAGUCreationDTO
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

	// Not possible to test this case as the hour is validated by the LocalTime class
//	@Test
//	fun `get daily gas measures at a certain hour for several days with invalid hour`() =
//		testWithTransactionManagerAndRollback { transactionManager ->
//			// arrange
//			val fetchService = FetchService(transactionManager)
//			val chronService = ChronService(transactionManager, fetchService)
//			val dnoService = DNOService(transactionManager)
//			val aguService = AGUService(transactionManager, aguDomain, chronService)
//			val dnoCreation = dummyDNODTO
//			val creationAgu = dummyAGUCreationDTO
//			val hour = LocalTime.of(25, 0) // TODO Check test fails here due to invalid hour 0 - 23
//			val days = 2
//
//			dnoService.createDNO(dnoCreation)
//			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
//
//			// act
//			val result = aguService.getDailyGasMeasures(agu.getSuccessOrThrow(), days, hour)
//
//			// assert
//			assert(result.isFailure())
//			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidTime)
//		}

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
			val creationAgu = dummyAGUCreationDTO
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
	fun `get hourly gas measures at a certain day for several hours with empty id`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val day = LocalDate.now()

			// act
			val result = aguService.getHourlyGasMeasures("", day)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidCUI)
		}

	@Test
	fun `get hourly gas measures at a certain day for several hours with un existing agu`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val day = LocalDate.now()

			// act
			val result = aguService.getHourlyGasMeasures("PT6543210987654321XX", day)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.AGUNotFound)
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
			val creationAgu = dummyAGUCreationDTO
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
	fun `get prediction gas measures for several days at a certain hour with empty id`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val days = 2
			val time = LocalTime.of(12, 0)

			// act
			val result = aguService.getPredictionGasLevels("", days, time)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidCUI)
		}

	@Test
	fun `get prediction gas measures for several days at a certain hour with un existing agu`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val days = 2
			val time = LocalTime.of(12, 0)

			// act
			val result = aguService.getPredictionGasLevels("PT6543210987654321XX", days, time)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is GetMeasuresError.AGUNotFound)
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
			val creationAgu = dummyAGUCreationDTO
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

	// Not possible to test this case as the hour is validated by the LocalTime class
//	@Test
//	fun `get prediction gas measures for several days at a certain hour with invalid hour`() =
//		testWithTransactionManagerAndRollback { transactionManager ->
//			// arrange
//			val fetchService = FetchService(transactionManager)
//			val chronService = ChronService(transactionManager, fetchService)
//			val dnoService = DNOService(transactionManager)
//			val aguService = AGUService(transactionManager, aguDomain, chronService)
//			val dnoCreation = dummyDNODTO
//			val creationAgu = dummyAGUCreationDTO
//			val days = 2
//			val time = LocalTime.of(25, 0) // TODO Check test fails here due to invalid hour 0 - 23
//
//			dnoService.createDNO(dnoCreation)
//			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
//
//			// act
//			val result = aguService.getPredictionGasLevels(agu.getSuccessOrThrow(), days, time)
//
//			// assert
//			assert(result.isFailure())
//			assert(result.getFailureOrThrow() is GetMeasuresError.InvalidTime)
//		}

	// TODO Needs test for provider error

	@Test
	fun `update agu's favorite state correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.updateFavouriteState(agu.getSuccessOrThrow(), true)

		// assert
		assert(result.isSuccess())
		assert(result.getSuccessOrThrow().isFavourite)
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
	fun `update agu's favorite state with empty id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateFavouriteState("", true)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateFavouriteStateError.AGUNotFound)
		}

	@Test
	fun `update agu's favorite state with un existing agu should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateFavouriteState("PT6543210987654321XX", true)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateFavouriteStateError.AGUNotFound)
		}

	@Test
	fun `update Active state in AGU correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		// TODO check if the change occurs inside the transaction
		//  or if we need another transaction to fetch the latest data,
		//		dark magic somehow does not want to update
		val result = aguService.updateActiveState(agu.getSuccessOrThrow(), false).also(::println)
		val dbAGU = aguService.getAGUById(agu.getSuccessOrThrow()).also(::println)
		// assert
		assert(result.isSuccess())
		assert(dbAGU.isSuccess())
		assertFalse(dbAGU.getSuccessOrThrow().isActive)
		assert(result.getSuccessOrThrow().isActive)
	}

	@Test
	fun `update Active state in AGU with invalid id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateActiveState("invalid", false)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateActiveStateError.AGUNotFound)
		}

	@Test
	fun `update Active state in AGU with empty id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateActiveState("", false)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateActiveStateError.AGUNotFound)
		}

	@Test
	fun `update Active state in AGU with un existing agu should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateActiveState("PT6543210987654321XX", false)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateActiveStateError.AGUNotFound)
		}

	@Test
	fun `add contact to AGU correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO
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
	fun `add contact to AGU with empty id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val contact = ServiceUtils.dummyLogisticContact

			// act
			val result = aguService.addContact("", contact)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddContactError.AGUNotFound)
		}

	@Test
	fun `add contact to AGU with un existing agu should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val contact = ServiceUtils.dummyLogisticContact

			// act
			val result = aguService.addContact("PT6543210987654321XX", contact)

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
			val dnoService = DNOService(transactionManager)
			val contact = ServiceUtils.dummyLogisticContact.copy(type = "invalid")
			dnoService.createDNO(dummyDNODTO)
			val aguCui = aguService.createAGU(dummyAGUCreationDTO).getSuccessOrThrow()

			// act
			val result = aguService.addContact(aguCui, contact)

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
			val dnoService = DNOService(transactionManager)
			val contact = ServiceUtils.dummyLogisticContact.copy(name = "")
			dnoService.createDNO(dummyDNODTO)
			val aguCui = aguService.createAGU(dummyAGUCreationDTO).getSuccessOrThrow()

			// act
			val result = aguService.addContact(aguCui, contact)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddContactError.InvalidContact)
		}

	@Test
	fun `add contact to AGU with empty contact phone number should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoService = DNOService(transactionManager)
			val contact = ServiceUtils.dummyLogisticContact.copy(phone = "")
			dnoService.createDNO(dummyDNODTO)
			val aguCui = aguService.createAGU(dummyAGUCreationDTO).getSuccessOrThrow()

			// act
			val result = aguService.addContact(aguCui, contact)

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
			val dnoService = DNOService(transactionManager)
			val contact = ServiceUtils.dummyLogisticContact.copy(phone = "invalid")
			val dno = dummyDNODTO

			dnoService.createDNO(dno)
			val aguCui = aguService.createAGU(dummyAGUCreationDTO).getSuccessOrThrow()

			// act
			val result = aguService.addContact(aguCui, contact)

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
		val creationAgu = dummyAGUCreationDTO
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
		val creationAgu = dummyAGUCreationDTO
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
			val contactId = Int.MIN_VALUE

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
			val creationAgu = dummyAGUCreationDTO
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
		val creationAgu = dummyAGUCreationDTO
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
			val creationAgu = dummyAGUCreationDTO
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
	fun `add tank to AGU with critical level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(levels = dummyTank.levels.copy(critical = dummyGasLevels.max + 1))

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddTankError.InvalidLevels)
		}

	@Test
	fun `add tank to AGU with min level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(levels = dummyTank.levels.copy(min = dummyGasLevels.max + 1))

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddTankError.InvalidLevels)
		}

	@Test
	fun `add tank to AGU with min level under critical level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(levels = dummyTank.levels.copy(min = dummyTank.levels.critical - 1))

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddTankError.InvalidLevels)
		}

	@Test
	fun `add tank with negative min level should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO
		val tank = dummyTank.copy(levels = dummyTank.levels.copy(min = -1))

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AddTankError.InvalidLevels)
	}

	@Test
	fun `add tank with negative critical level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(levels = dummyTank.levels.copy(critical = -1))

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is AddTankError.InvalidLevels)
		}

	@Test
	fun `add tank with negative max level should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO
		val tank = dummyTank.copy(levels = dummyTank.levels.copy(max = -1))

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AddTankError.InvalidLevels)
	}

	@Test
	fun `add tank with invalid capacity should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO
		val tank = dummyTank.copy(capacity = -1)

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

		// act
		val result = aguService.addTank(agu.getSuccessOrThrow(), tank)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AddTankError.InvalidCapacity)
	}

	@Test
	fun `add tank twice should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO
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
		val creationAgu = dummyAGUCreationDTO
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
		val result = aguService.updateTank("invalid", 2, updateTankDTO)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is UpdateTankError.InvalidCUI)
	}

	@Test
	fun `update tank with empty agu id should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)

		// act
		val result = aguService.updateTank("", 2, updateTankDTO)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is UpdateTankError.InvalidCUI)
	}

	@Test
	fun `update tank with critical level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(number = 2)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
			val addedTank = aguService.addTank(agu.getSuccessOrThrow(), tank).getSuccessOrThrow()

			// act
			val result = aguService.updateTank(
				agu.getSuccessOrThrow(),
				addedTank,
				updateTankDTO.copy(criticalLevel = dummyGasLevels.max + 1)
			)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidLevels)
		}

	@Test
	fun `update tank with min level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(number = 2)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
			val addedTank = aguService.addTank(agu.getSuccessOrThrow(), tank).getSuccessOrThrow()

			// act
			val result = aguService.updateTank(
				agu.getSuccessOrThrow(),
				addedTank,
				updateTankDTO.copy(minLevel = dummyGasLevels.max + 1)
			)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidLevels)
		}

	@Test
	fun `update tank with min level under critical level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(number = 2)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
			val addedTank = aguService.addTank(agu.getSuccessOrThrow(), tank).getSuccessOrThrow()

			// act
			val result = aguService.updateTank(
				agu.getSuccessOrThrow(),
				addedTank,
				updateTankDTO.copy(minLevel = dummyGasLevels.critical - 1)
			)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidLevels)
		}

	@Test
	fun `update tank with negative min level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(number = 2)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
			val addedTank = aguService.addTank(agu.getSuccessOrThrow(), tank).getSuccessOrThrow()

			// act
			val result = aguService.updateTank(agu.getSuccessOrThrow(), addedTank, updateTankDTO.copy(minLevel = -1))

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidLevels)
		}

	@Test
	fun `update tank with negative critical level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(number = 2)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
			val addedTank = aguService.addTank(agu.getSuccessOrThrow(), tank).getSuccessOrThrow()

			// act
			val result =
				aguService.updateTank(agu.getSuccessOrThrow(), addedTank, updateTankDTO.copy(criticalLevel = -1))

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidLevels)
		}

	@Test
	fun `update tank with negative max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO
			val tank = dummyTank.copy(number = 2)

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))
			val addedTank = aguService.addTank(agu.getSuccessOrThrow(), tank).getSuccessOrThrow()

			// act
			val result = aguService.updateTank(agu.getSuccessOrThrow(), addedTank, updateTankDTO.copy(maxLevel = -1))

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidLevels)
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
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateTank(agu.getSuccessOrThrow(), -1, updateTankDTO)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidTankNumber)
		}

	@Test
	fun `update tank with invalid tank capacity should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateTank(agu.getSuccessOrThrow(), 1, updateTankDTO.copy(capacity = -1))

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateTankError.InvalidCapacity)
		}

	@Test
	fun `update AGU Gas Levels correctly`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoService = DNOService(transactionManager)

		val dnoCreation = dummyDNODTO
		val creationAgu = dummyAGUCreationDTO

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
	fun `update AGU gas levels with empty AGU id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateGasLevels("", dummyGasLevelsDTO)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateGasLevelsError.AGUNotFound)
		}

	@Test
	fun `update AGU Gas Levels with critical level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateGasLevels(
				agu.getSuccessOrThrow(),
				dummyGasLevelsDTO.copy(critical = dummyGasLevelsDTO.max + 1)
			)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateGasLevelsError.InvalidLevels)
		}

	@Test
	fun `update AGU gas levels with min level over max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateGasLevels(
				agu.getSuccessOrThrow(),
				dummyGasLevelsDTO.copy(min = dummyGasLevelsDTO.max + 1)
			)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateGasLevelsError.InvalidLevels)
		}

	@Test
	fun `update AGU gas levels with min level under critical level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateGasLevels(
				agu.getSuccessOrThrow(),
				dummyGasLevelsDTO.copy(min = dummyGasLevelsDTO.critical - 1)
			)

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateGasLevelsError.InvalidLevels)
		}

	@Test
	fun `update AGU gas levels with negative min level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateGasLevels(agu.getSuccessOrThrow(), dummyGasLevelsDTO.copy(min = -1))

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateGasLevelsError.InvalidLevels)
		}

	@Test
	fun `update AGU gas levels with negative critical level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateGasLevels(agu.getSuccessOrThrow(), dummyGasLevelsDTO.copy(critical = -1))

			// assert
			assert(result.isFailure())
			assert(result.getFailureOrThrow() is UpdateGasLevelsError.InvalidLevels)
		}

	@Test
	fun `update AGU gas levels with negative max level should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val dnoService = DNOService(transactionManager)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			val dnoCreation = dummyDNODTO
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateGasLevels(agu.getSuccessOrThrow(), dummyGasLevelsDTO.copy(max = -1))

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
		val creationAgu = dummyAGUCreationDTO

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
	fun `update AGU notes with empty AGU id should fail`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)

			// act
			val result = aguService.updateNotes("", "new notes")

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
			val creationAgu = dummyAGUCreationDTO

			dnoService.createDNO(dnoCreation)
			val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation.name))

			// act
			val result = aguService.updateNotes(agu.getSuccessOrThrow(), "")

			// assert
			assert(result.isSuccess())
			assert(result.getSuccessOrThrow().notes == "")
		}
}