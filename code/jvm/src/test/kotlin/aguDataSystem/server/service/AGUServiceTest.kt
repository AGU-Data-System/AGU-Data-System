package aguDataSystem.server.service

import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.service.ServiceUtils.dummyAGUCreationDTO
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.dno.DNOService
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithTransactionManagerAndRollback
import aguDataSystem.utils.isSuccess
import kotlin.test.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class AGUServiceTest {

	private val aguDomain = AGUDomain()

	@Test
	fun `create AGU with valid data`() = testWithTransactionManagerAndRollback { transactionManager ->
		// arrange
		val dnoService = DNOService(transactionManager)
		val aguService = AGUService(transactionManager, aguDomain)
		val dnoCreation = ServiceUtils.dummyDNOName
		val creationAgu = dummyAGUCreationDTO.copy(tanks = listOf(ServiceUtils.dummyTank))

		dnoService.createDNO(dnoCreation)
		// act
		val result = aguService.createAGU(creationAgu).also { println(it) }

		// assert
		assert(result.isSuccess())
	}

}