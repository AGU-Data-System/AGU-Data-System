package aguDataSystem.server.repository

import aguDataSystem.server.repository.RepositoryUtils.dummyDNO
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIDNORepositoryTest {

	@Test
	fun `addDNO Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)
		val dno = dnoRepository.getByName(sut.name)

		// assert
		assertNotNull(dno)
		assertEquals(sut.name, dno.name)
	}

	@Test
	fun `addDNO with an existing DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)

		// assert
		assertFailsWith<UnableToExecuteStatementException> {
			dnoRepository.addDNO(sut)
		}
	}

	@Test
	fun `addDNO with an empty DNO name`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO.copy(name = "")

		// act
		assertFailsWith<UnableToExecuteStatementException> {
			dnoRepository.addDNO(sut)
		}
	}

	@Test
	fun `addDNO with empty region`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO.copy(region = "")

		// act
		assertFailsWith<UnableToExecuteStatementException> {
			dnoRepository.addDNO(sut)
		}
	}

	@Test
	fun `getByName Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)
		val dno = dnoRepository.getByName(sut.name)

		// assert
		assertNotNull(dno)
		assertEquals(sut.name, dno.name)
	}

	@Test
	fun `getByName with an un-existing DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = "123"

		// act
		val dno = dnoRepository.getByName(sut)

		// assert
		assertNull(dno)
	}

	@Test
	fun `getByName with an empty DNO name`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = ""

		// act
		val dno = dnoRepository.getByName(sut)

		// assert
		assertNull(dno)
	}

	@Test
	fun `getById Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		val addedDNO = dnoRepository.addDNO(sut)
		val dnoById = dnoRepository.getById(addedDNO.id)

		// assert
		assertNotNull(dnoById)
		assertEquals(sut.name, dnoById.name)
	}

	@Test
	fun `getById with an un-existing DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = Int.MIN_VALUE

		// act
		val dno = dnoRepository.getById(sut)

		// assert
		assertNull(dno)
	}

	@Test
	fun `isDNOStoredByName Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)
		val isDNOStored = dnoRepository.isDNOStoredByName(sut.name)

		// assert
		assertTrue(isDNOStored)
	}

	@Test
	fun `isDNOStoredByName with an un-existing DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = Int.MIN_VALUE.toString()

		// act
		val isDNOStored = dnoRepository.isDNOStoredByName(sut)

		// assert
		assertTrue(!isDNOStored)
	}

	@Test
	fun `isDNOStoredByName with an empty DNO name`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = ""

		// act
		val isDNOStored = dnoRepository.isDNOStoredByName(sut)

		// assert
		assertTrue(!isDNOStored)
	}

	@Test
	fun `getAll Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)
		val dnos = dnoRepository.getAll()

		// assert
		assertTrue(dnos.isNotEmpty())
	}

	@Test
	fun `getAll with no DNOs`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)

		// act
		val dnos = dnoRepository.getAll()

		// assert
		assertTrue(dnos.isEmpty())
	}

	@Test
	fun `deleteDNO Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		val addedDNO = dnoRepository.addDNO(sut)
		dnoRepository.deleteDNO(addedDNO.id)
		val dno = dnoRepository.getById(addedDNO.id)

		// assert
		assertNull(dno)
	}

	@Test
	fun `deleteDNO with an un-existing DNO does nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = Int.MIN_VALUE

		// act
		dnoRepository.deleteDNO(sut)
		val dno = dnoRepository.getById(sut)

		// assert
		assertNull(dno)
	}
}
