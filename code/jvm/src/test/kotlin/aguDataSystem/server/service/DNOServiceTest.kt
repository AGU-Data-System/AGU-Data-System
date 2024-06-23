package aguDataSystem.server.service

import aguDataSystem.server.repository.RepositoryUtils.dummyDNO
import aguDataSystem.server.service.dno.DNOService
import aguDataSystem.server.service.errors.dno.CreateDNOError
import aguDataSystem.server.service.errors.dno.GetDNOError
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import aguDataSystem.utils.getFailureOrThrow
import aguDataSystem.utils.getSuccessOrThrow
import aguDataSystem.utils.isFailure
import aguDataSystem.utils.isSuccess
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class DNOServiceTest {

	@Test
	fun `create DNO with valid data`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO

		// act
		val result = dnoService.createDNO(creationDno)

		// assert
		assert(result.isSuccess())
	}

	@Test
	fun `create DNO with empty name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO.copy(name = "")

		// act
		val result = dnoService.createDNO(creationDno)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is CreateDNOError.InvalidName)
	}

	@Test
	fun `create DNO with same name twice should fail`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO

		// act
		val result = dnoService.createDNO(creationDno)
		val result2 = dnoService.createDNO(creationDno)

		// assert
		assert(result.isSuccess())
		assert(result2.isFailure())
		assert(result2.getFailureOrThrow() is CreateDNOError.DNOAlreadyExists)
	}

	@Test
	fun `get DNO By name with valid name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO
		dnoService.createDNO(creationDno)

		// act
		val result = dnoService.getDNOByName(creationDno.name)

		// assert
		assert(result.isSuccess())
		assert(result.getSuccessOrThrow().name == creationDno.name)
	}

	@Test
	fun `get Dno By name with invalid name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO
		dnoService.createDNO(creationDno)

		// act
		val result = dnoService.getDNOByName("invalidName")

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is GetDNOError.DNONotFound)
	}

	@Test
	fun `is DNO Stored By Name with valid name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO
		dnoService.createDNO(creationDno)

		// act
		val result = dnoService.isDNOStoredByName(creationDno.name)

		// assert
		assert(result)
	}

	@Test
	fun `is Dno Stored By Name with invalid name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO
		dnoService.createDNO(creationDno)

		// act
		val result = dnoService.isDNOStoredByName("invalidName")

		// assert
		assert(!result)
	}

	@Test
	fun `get Dno By Id with valid id`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO
		val dno = dnoService.createDNO(creationDno).getSuccessOrThrow()

		// act
		val result = dnoService.getDNOById(dno.id)

		// assert
		assert(result.isSuccess())
		assert(result.getSuccessOrThrow().name == creationDno.name)
	}

	@Test
	fun `get Dno By Id with invalid id`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO
		dnoService.createDNO(creationDno)

		// act
		val result = dnoService.getDNOById(Int.MIN_VALUE)

		// assert
		assert(result.isFailure())
		assert(result.getFailureOrThrow() is GetDNOError.DNONotFound)
	}

	@Test
	fun `delete DNO by valid id`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val creationDno = dummyDNO
		val dno = dnoService.createDNO(creationDno).getSuccessOrThrow()

		// act
		val deleteResult = dnoService.deleteDNO(dno.id)

		// assert
		assert(deleteResult.isSuccess())
		val getResult = dnoService.getDNOById(dno.id)
		assert(getResult.isFailure())
		assert(getResult.getFailureOrThrow() is GetDNOError.DNONotFound)
	}

	@Test
	fun `delete DNO by invalid id`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)

		// act
		val result = dnoService.deleteDNO(Int.MIN_VALUE)

		// assert
		assert(result.isSuccess())
	}
}
