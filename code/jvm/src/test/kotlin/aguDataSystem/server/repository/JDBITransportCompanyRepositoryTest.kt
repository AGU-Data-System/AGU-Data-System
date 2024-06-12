package aguDataSystem.server.repository

import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.repository.RepositoryUtils.DUMMY_TRANSPORT_COMPANY_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyDNO
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.transportCompany.JDBITransportCompanyRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBITransportCompanyRepositoryTest {

	@Test
	fun `add transport company correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

		// act
		val id = repository.addTransportCompany(tCompany)
		val all = repository.getTransportCompanies()

		// assert
		assert(all.size == 1)
		assertContains(all, TransportCompany(id, tCompany))
	}

	@Test
	fun `add transport company with empty name should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = ""

		// act and assert
		assertFailsWith<UnableToExecuteStatementException> {
			repository.addTransportCompany(tCompany)
		}
	}

	@Test
	fun `add transport company twice should throw exception`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

		// act
		repository.addTransportCompany(tCompany)

		// act and assert
		assertFailsWith<UnableToExecuteStatementException> {
			repository.addTransportCompany(tCompany)
		}
	}

	@Test
	fun `get transport company by name correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
		repository.addTransportCompany(tCompany)

		// act
		val result = repository.getTransportCompanyByName(tCompany)

		// assert
		assertNotNull(result)
		assertEquals(result.name, tCompany)
	}

	@Test
	fun `get transport company by name with non existing name should return null`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val repository = JDBITransportCompanyRepository(handle)
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
			repository.addTransportCompany(tCompany)

			// act
			val result = repository.getTransportCompanyByName("Non existing name")

			// assert
			assertNull(result)
		}

	@Test
	fun `get transport company with empty name should return null`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
		repository.addTransportCompany(tCompany)

		// act
		val result = repository.getTransportCompanyByName("")

		// assert
		assertNull(result)
	}

	@Test
	fun `get transport company by id correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
		val id = repository.addTransportCompany(tCompany)

		// act
		val result = repository.getTransportCompanyById(id)

		// assert
		assertNotNull(result)
		assertEquals(result.name, tCompany)
	}

	@Test
	fun `get transport company by id with non existing id should return null`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
		repository.addTransportCompany(tCompany)

		// act
		val result = repository.getTransportCompanyById(Int.MIN_VALUE)

		// assert
		assertNull(result)
	}

	@Test
	fun `get transport companies by AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val transportRepository = JDBITransportCompanyRepository(handle)
		val dno = dummyDNO
		val agu = dummyAGU
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

		val dnoId = dnoRepository.addDNO(dno).id
		val aguCui = aguRepository.addAGU(agu, dnoId)
		val transportCompanyId = transportRepository.addTransportCompany(tCompany)

		transportRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)

		// act
		val result = transportRepository.getTransportCompaniesByAGU(aguCui)

		// assert
		assert(result.isNotEmpty())
		assertEquals(result.first().name, tCompany)
	}

	@Test
	fun `get transport companies by AGU with non existing AGU should return empty list`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportRepository = JDBITransportCompanyRepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			val aguCui = aguRepository.addAGU(agu, dnoId)
			val transportCompanyId = transportRepository.addTransportCompany(tCompany)

			transportRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)

			// act
			val result = transportRepository.getTransportCompaniesByAGU("Non existing AGU")

			// assert
			assert(result.isEmpty())
		}

	@Test
	fun `get transport companies by AGU with empty AGU should return empty list`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportRepository = JDBITransportCompanyRepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			val aguCui = aguRepository.addAGU(agu, dnoId)
			val transportCompanyId = transportRepository.addTransportCompany(tCompany)

			transportRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)

			// act
			val result = transportRepository.getTransportCompaniesByAGU("")

			// assert
			assert(result.isEmpty())
		}

	@Test
	fun `delete transport company correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
		val id = repository.addTransportCompany(tCompany)

		// act
		repository.deleteTransportCompany(id)
		val result = repository.getTransportCompanies()

		// assert
		assert(result.isEmpty())
	}

	@Test
	fun `delete transport company with non existing id should do nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
		repository.addTransportCompany(tCompany)
		val dummyId = Int.MIN_VALUE

		// act
		repository.deleteTransportCompany(dummyId)
		val result = repository.getTransportCompanies()

		// assert
		assert(result.isNotEmpty())
		assertFalse(result.any { it.id == dummyId })
	}

	@Test
	fun `get all transport companies correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany1 = DUMMY_TRANSPORT_COMPANY_NAME
		val tCompany2 = DUMMY_TRANSPORT_COMPANY_NAME + "2"
		repository.addTransportCompany(tCompany1)
		repository.addTransportCompany(tCompany2)

		// act
		val result = repository.getTransportCompanies()

		// assert
		assert(result.size == 2)
		assertContains(result.map { it.name }, tCompany1)
		assertContains(result.map { it.name }, tCompany2)
	}

	@Test
	fun `get all transport companies with no companies should return empty list`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val repository = JDBITransportCompanyRepository(handle)

			// act
			val result = repository.getTransportCompanies()

			// assert
			assert(result.isEmpty())
		}

	@Test
	fun `add transport company to AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val transportRepository = JDBITransportCompanyRepository(handle)
		val dno = dummyDNO
		val agu = dummyAGU
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

		val dnoId = dnoRepository.addDNO(dno).id
		val aguCui = aguRepository.addAGU(agu, dnoId)
		val transportCompanyId = transportRepository.addTransportCompany(tCompany)

		// act
		transportRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)
		val result = transportRepository.getTransportCompaniesByAGU(aguCui)

		// assert
		assert(result.isNotEmpty())
		assertEquals(result.first().name, tCompany)
	}

	@Test
	fun `add transport company to agu with non existing agu should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportRepository = JDBITransportCompanyRepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			aguRepository.addAGU(agu, dnoId)
			val transportCompanyId = transportRepository.addTransportCompany(tCompany)

			// act and assert
			assertFailsWith<UnableToExecuteStatementException> {
				transportRepository.addTransportCompanyToAGU("Non existing AGU", transportCompanyId)
			}
		}

	@Test
	fun `add transport company to agu with empty agu cui should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportRepository = JDBITransportCompanyRepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			aguRepository.addAGU(agu, dnoId)
			val transportCompanyId = transportRepository.addTransportCompany(tCompany)

			// act and assert
			assertFailsWith<UnableToExecuteStatementException> {
				transportRepository.addTransportCompanyToAGU("", transportCompanyId)
			}
		}

	@Test
	fun `add transport company to agu with non existing transport company should throw exception`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportRepository = JDBITransportCompanyRepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			val aguCui = aguRepository.addAGU(agu, dnoId)
			transportRepository.addTransportCompany(tCompany)

			// act and assert
			assertFailsWith<UnableToExecuteStatementException> {
				transportRepository.addTransportCompanyToAGU(aguCui, Int.MIN_VALUE)
			}
		}

	@Test
	fun `delete transport company from AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val transportRepository = JDBITransportCompanyRepository(handle)
		val dno = dummyDNO
		val agu = dummyAGU
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

		val dnoId = dnoRepository.addDNO(dno).id
		val aguCui = aguRepository.addAGU(agu, dnoId)
		val transportCompanyId = transportRepository.addTransportCompany(tCompany)
		transportRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)

		// act
		transportRepository.deleteTransportCompanyFromAGU(aguCui, transportCompanyId)
		val result = transportRepository.getTransportCompaniesByAGU(aguCui)

		// assert
		assert(result.isEmpty())
	}

	@Test
	fun `delete transport company from AGU with non existing transport company should do nothing`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportRepository = JDBITransportCompanyRepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			val aguCui = aguRepository.addAGU(agu, dnoId)
			val transportCompanyId = transportRepository.addTransportCompany(tCompany)
			transportRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)

			// act
			transportRepository.deleteTransportCompanyFromAGU(aguCui, Int.MIN_VALUE)
			val result = transportRepository.getTransportCompaniesByAGU(aguCui)

			// assert
			assert(result.isNotEmpty())
		}

	@Test
	fun `delete transport company from AGU with non existing AGU should do nothing`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportRepository = JDBITransportCompanyRepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			val aguCui = aguRepository.addAGU(agu, dnoId)
			val transportCompanyId = transportRepository.addTransportCompany(tCompany)
			transportRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)

			// act
			transportRepository.deleteTransportCompanyFromAGU("Non existing AGU", transportCompanyId)
			val result = transportRepository.getTransportCompaniesByAGU(aguCui)

			// assert
			assert(result.isNotEmpty())
		}

	@Test
	fun `delete transport company from AGU with empty AGU cui should do nothing`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val dnoRepository = JDBIDNORepository(handle)
			val aguRepository = JDBIAGURepository(handle)
			val transportRepository = JDBITransportCompanyRepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME

			val dnoId = dnoRepository.addDNO(dno).id
			val aguCui = aguRepository.addAGU(agu, dnoId)
			val transportCompanyId = transportRepository.addTransportCompany(tCompany)
			transportRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)

			// act
			transportRepository.deleteTransportCompanyFromAGU("", transportCompanyId)
			val result = transportRepository.getTransportCompaniesByAGU(aguCui)

			// assert
			assert(result.isNotEmpty())
		}

	@Test
	fun `isTransportCompanyStoredById correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
		val id = repository.addTransportCompany(tCompany)

		// act
		val result = repository.isTransportCompanyStoredById(id)

		// assert
		assert(result)
	}

	@Test
	fun `isTransportCompanyStoredById with non existing id should return false`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val repository = JDBITransportCompanyRepository(handle)
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
			repository.addTransportCompany(tCompany)

			// act
			val result = repository.isTransportCompanyStoredById(Int.MIN_VALUE)

			// assert
			assertFalse(result)
		}

	@Test
	fun `isTransportCompanyStoredByName correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val repository = JDBITransportCompanyRepository(handle)
		val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
		repository.addTransportCompany(tCompany)

		// act
		val result = repository.isTransportCompanyStoredByName(tCompany)

		// assert
		assert(result)
	}

	@Test
	fun `isTransportCompanyStoredByName with non existing name should return false`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val repository = JDBITransportCompanyRepository(handle)
			val tCompany = DUMMY_TRANSPORT_COMPANY_NAME
			repository.addTransportCompany(tCompany)

			// act
			val result = repository.isTransportCompanyStoredByName("Non existing name")

			// assert
			assertFalse(result)
		}
}