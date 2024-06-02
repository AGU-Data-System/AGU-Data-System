package aguDataSystem.server.service

import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.service.ServiceUtils.dummyAGUCreationDTO
import aguDataSystem.server.service.ServiceUtils.dummyDNOName
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.chron.ChronService
import aguDataSystem.server.service.chron.FetchService
import aguDataSystem.server.service.dno.DNOService
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import aguDataSystem.utils.getFailureOrThrow
import aguDataSystem.utils.getSuccessOrThrow
import aguDataSystem.utils.isFailure
import aguDataSystem.utils.isSuccess
import kotlin.test.Test
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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
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
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

		// act
		val result = aguService.createAGU(creationAgu).also(::println)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidDNO)
	}

	@Test
	fun `create AGU with invalid DNO`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu.copy(dnoName = "invalid"))

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidDNO)
	}

	@Test
	fun `create AGU with invalid coordinates`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(
			tanks = listOf(ServiceUtils.dummyTank),
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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(
			tanks = listOf(ServiceUtils.dummyTank),
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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(
			tanks = listOf(ServiceUtils.dummyTank),
			contacts = listOf(ServiceUtils.dummyLogisticContact.copy(phone = ""))
		)

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is AGUCreationError.InvalidContact)
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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation))

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
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

		dnoService.createDNO(dnoCreation)
		aguService.createAGU(creationAgu.copy(dnoName = dnoCreation))

		// act
		val result = aguService.getAGUById("invalid")

		// assert
		assert(result.isFailure())
	}

	@Test
	fun `get temperature measures by agu id and days`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val dnoCreation = dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))
		val days = 2

		dnoService.createDNO(dnoCreation)
		val agu = aguService.createAGU(creationAgu.copy(dnoName = dnoCreation))

		// act
		val result = aguService.getTemperatureMeasures(agu.getSuccessOrThrow(), days)

		// assert
		assert(result.isSuccess())
	}
}