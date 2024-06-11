package aguDataSystem.server.service

import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.service.ServiceUtils.dummyAGUCreationDTO
import aguDataSystem.server.service.ServiceUtils.dummyDNODTO
import aguDataSystem.server.service.ServiceUtils.dummyTank
import aguDataSystem.server.service.ServiceUtils.dummyTransportCompany
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.chron.ChronService
import aguDataSystem.server.service.chron.FetchService
import aguDataSystem.server.service.dno.DNOService
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyError
import aguDataSystem.server.service.errors.transportCompany.GetTransportCompaniesOfAGUError
import aguDataSystem.server.service.transportCompany.TransportCompanyService
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import aguDataSystem.utils.getFailureOrThrow
import aguDataSystem.utils.getSuccessOrThrow
import aguDataSystem.utils.isFailure
import aguDataSystem.utils.isSuccess
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class TransportCompanyServiceTest {

	private val aguDomain = AGUDomain()

	@Test
	fun `get transport companies`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val transportCompanyService = TransportCompanyService(transactionManager)
		val sut = dummyTransportCompany
		transportCompanyService.addTransportCompany(sut)

		// act
		val transportCompanies = transportCompanyService.getTransportCompanies()

		// assert
		assertTrue(transportCompanies.isNotEmpty())
		assertContains(transportCompanies.map { it.name }, sut.name)
	}

	@Test
	fun `get transport companies without any transport company`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val transportCompanyService = TransportCompanyService(transactionManager)

			// act
			val transportCompanies = transportCompanyService.getTransportCompanies()

			// assert
			assertTrue(transportCompanies.isEmpty())
		}

	@Test
	fun `get transport companies of an AGU`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val fetchService = FetchService(transactionManager)
		val chronService = ChronService(transactionManager, fetchService)
		val aguService = AGUService(transactionManager, aguDomain, chronService)
		val transportCompanyService = TransportCompanyService(transactionManager)
		val dno = dummyDNODTO
		val agu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))
		val sut = dummyTransportCompany

		dnoService.createDNO(dno).getSuccessOrThrow().id
		val aguCui = aguService.createAGU(agu).getSuccessOrThrow()

		val transportId = transportCompanyService.addTransportCompany(sut).getSuccessOrThrow()
		transportCompanyService.addTransportCompanyToAGU(aguCui, transportId)

		// act
		val transportCompanies = transportCompanyService.getTransportCompaniesOfAGU(aguCui)

		// assert
		assertTrue(transportCompanies.isSuccess())
		assertTrue(transportCompanies.getSuccessOrThrow().isNotEmpty())
		assertContains(transportCompanies.getSuccessOrThrow().map { it.name }, sut.name)
	}

	@Test
	fun `get transport companies of an AGU without any transport company`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val dnoService = DNOService(transactionManager)
			val fetchService = FetchService(transactionManager)
			val chronService = ChronService(transactionManager, fetchService)
			val aguService = AGUService(transactionManager, aguDomain, chronService)
			val transportCompanyService = TransportCompanyService(transactionManager)
			val dno = dummyDNODTO
			val agu = dummyAGUCreationDTO.copy(tanks = listOf(dummyTank))

			dnoService.createDNO(dno).getSuccessOrThrow().id
			val aguCui = aguService.createAGU(agu).getSuccessOrThrow()

			// act
			val transportCompanies = transportCompanyService.getTransportCompaniesOfAGU(aguCui)

			// assert
			assertTrue(transportCompanies.isSuccess())
			assertTrue(transportCompanies.getSuccessOrThrow().isEmpty())
		}

	@Test
	fun `get transport companies of an un existing AGU`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val transportCompanyService = TransportCompanyService(transactionManager)

			// act
			val transportCompanies = transportCompanyService.getTransportCompaniesOfAGU("unexisting")

			// assert
			assertTrue(transportCompanies.isFailure())
			assertTrue(transportCompanies.getFailureOrThrow() is GetTransportCompaniesOfAGUError.AGUNotFound)
		}

	@Test
	fun `get transport companies with a empty AGU cui`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val transportCompanyService = TransportCompanyService(transactionManager)

		// act
		val transportCompanies = transportCompanyService.getTransportCompaniesOfAGU("")

		// assert
		assertTrue(transportCompanies.isFailure())
		assertTrue(transportCompanies.getFailureOrThrow() is GetTransportCompaniesOfAGUError.AGUNotFound)
	}

	@Test
	fun `add transport company`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val transportCompanyService = TransportCompanyService(transactionManager)
		val sut = dummyTransportCompany

		// act
		val result = transportCompanyService.addTransportCompany(sut)

		// assert
		assertTrue(result.isSuccess())
	}

	@Test
	fun `add transport company with an empty name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val transportCompanyService = TransportCompanyService(transactionManager)
		val sut = dummyTransportCompany.copy(name = "")

		// act
		val result = transportCompanyService.addTransportCompany(sut)

		// assert
		assertTrue(result.isFailure())
		assertTrue(result.getFailureOrThrow() is AddTransportCompanyError.InvalidName)
	}

	@Test
	fun `add transport company with an existing name`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val transportCompanyService = TransportCompanyService(transactionManager)
		val sut = dummyTransportCompany

		transportCompanyService.addTransportCompany(sut)

		// act
		val result = transportCompanyService.addTransportCompany(sut)

		// assert
		assertTrue(result.isFailure())
		assertTrue(result.getFailureOrThrow() is AddTransportCompanyError.TransportCompanyAlreadyExists)
	}

	@Test
	fun `add transport company with an existing name case insensitive`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val transportCompanyService = TransportCompanyService(transactionManager)
			val sut = dummyTransportCompany

			transportCompanyService.addTransportCompany(sut)

			// act
			val result = transportCompanyService.addTransportCompany(sut.copy(name = sut.name.uppercase()))

			// assert
			assertTrue(result.isFailure())
			assertTrue(result.getFailureOrThrow() is AddTransportCompanyError.TransportCompanyAlreadyExists)
		}

	@Test
	fun `add transport company with an existing name case insensitive 2`() =
		testWithTransactionManagerAndRollback { transactionManager ->
			// arrange
			val transportCompanyService = TransportCompanyService(transactionManager)
			val sut = dummyTransportCompany

			transportCompanyService.addTransportCompany(sut)

			// act
			val result = transportCompanyService.addTransportCompany(sut.copy(name = sut.name.lowercase()))

			// assert
			assertTrue(result.isFailure())
			assertTrue(result.getFailureOrThrow() is AddTransportCompanyError.TransportCompanyAlreadyExists)
		}
}