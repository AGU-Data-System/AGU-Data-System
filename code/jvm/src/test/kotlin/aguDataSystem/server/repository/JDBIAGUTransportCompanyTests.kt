package aguDataSystem.server.repository

import aguDataSystem.server.repository.RepositoryUtils.DUMMY_TRANSPORT_COMPANY_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyDNO
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.transportCompany.JDBITransportCompanyRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIAGUTransportCompanyTests {
	@Test
	fun `get AGU with transport companies`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val transportCompanyRepository = JDBITransportCompanyRepository(handle)

		val dno = dummyDNO
		val agu = dummyAGU
		val transportCompany = DUMMY_TRANSPORT_COMPANY_NAME

		val dnoId = dnoRepository.addDNO(dno).id
		val aguCui = aguRepository.addAGU(agu, dnoId)
		val transportCompanyId = transportCompanyRepository.addTransportCompany(transportCompany)

		transportCompanyRepository.addTransportCompanyToAGU(agu.cui, transportCompanyId)

		// act
		val result = aguRepository.getAGUsBasicInfo().first { it.cui == aguCui }

		// assert
		assertNotNull(result)
		assertEquals(1, result.transportCompanies.size)
		assertEquals(transportCompany, result.transportCompanies.first().name)
	}

	@Test
	fun `get AGUs basic info with several AGUs and several transport companies`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportCompanyRepository = JDBITransportCompanyRepository(handle)

			val dno = dummyDNO
			val agu = dummyAGU
			val transportCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			val aguCui = aguRepository.addAGU(agu, dnoId)
			val transportCompanyId = transportCompanyRepository.addTransportCompany(transportCompany)

			transportCompanyRepository.addTransportCompanyToAGU(agu.cui, transportCompanyId)

			val agu2 = dummyAGU.copy(cui = "PT6543210987654321XX", name = "AGU2", eic = "EIC2")
			val aguCui2 = aguRepository.addAGU(agu2, dnoId)
			val transportCompany2 = DUMMY_TRANSPORT_COMPANY_NAME + "2"
			val transportCompanyId2 = transportCompanyRepository.addTransportCompany(transportCompany2)

			transportCompanyRepository.addTransportCompanyToAGU(agu2.cui, transportCompanyId2)

			// act
			val result = aguRepository.getAGUsBasicInfo()

			// assert
			assertEquals(2, result.size)
			assertEquals(1, result.first { it.cui == aguCui }.transportCompanies.size)
			assertEquals(1, result.first { it.cui == aguCui2 }.transportCompanies.size)
		}

	@Test
	fun `add transport company to AGU and retrieve`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val transportCompanyRepository = JDBITransportCompanyRepository(handle)

		val dnoId = dnoRepository.addDNO(dummyDNO).id
		val aguCui = aguRepository.addAGU(dummyAGU, dnoId)
		val transportCompanyName = DUMMY_TRANSPORT_COMPANY_NAME
		val transportCompanyId = transportCompanyRepository.addTransportCompany(transportCompanyName)

		// act
		transportCompanyRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)
		val transportCompanies = transportCompanyRepository.getTransportCompaniesByAGU(aguCui)

		// assert
		assertEquals(1, transportCompanies.size)
		assertEquals(transportCompanyName, transportCompanies.first().name)
	}

	@Test
	fun `remove transport company from AGU`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val transportCompanyRepository = JDBITransportCompanyRepository(handle)

		val dnoId = dnoRepository.addDNO(dummyDNO).id
		val aguCui = aguRepository.addAGU(dummyAGU, dnoId)
		val transportCompanyName = DUMMY_TRANSPORT_COMPANY_NAME
		val transportCompanyId = transportCompanyRepository.addTransportCompany(transportCompanyName)

		transportCompanyRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)
		assertEquals(1, transportCompanyRepository.getTransportCompaniesByAGU(aguCui).size)

		// act
		transportCompanyRepository.deleteTransportCompanyFromAGU(aguCui, transportCompanyId)
		val transportCompanies = transportCompanyRepository.getTransportCompaniesByAGU(aguCui)

		// assert
		assertEquals(0, transportCompanies.size)
	}
}