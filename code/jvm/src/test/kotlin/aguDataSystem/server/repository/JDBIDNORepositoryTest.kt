package aguDataSystem.server.repository

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

	private val dummyDNO = "TEST_DNO"

	@Test
	fun `addDNO Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)
		val dno = dnoRepository.getByName(sut)

		// assert
		assertNotNull(dno)
		assertEquals(sut, dno.name)
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
	fun `getByName Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)
		val dno = dnoRepository.getByName(sut)

		// assert
		assertNotNull(dno)
		assertEquals(sut, dno.name)
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
	fun `getById Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)
		val dnoByName = dnoRepository.getByName(sut)
		requireNotNull(dnoByName)

		val dnoById = dnoRepository.getById(dnoByName.id)

		// assert
		assertNotNull(dnoById)
		assertEquals(sut, dnoByName.name)
	}

	@Test
	fun `getById with an un-existing DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = Int.MAX_VALUE

		// act
		val dno = dnoRepository.getById(sut)

		// assert
		assertNull(dno)
	}

	@Test
	fun `isDNOStored Correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = dummyDNO

		// act
		dnoRepository.addDNO(sut)
		val isDNOStored = dnoRepository.isDNOStored(sut)

		// assert
		assertTrue(isDNOStored)
	}

	@Test
	fun `isDNOStored with an un-existing DNO`() = testWithHandleAndRollback { handle ->
		// arrange
		val dnoRepository = JDBIDNORepository(handle)
		val sut = Int.MAX_VALUE.toString()

		// act
		val isDNOStored = dnoRepository.isDNOStored(sut)

		// assert
		assertTrue(!isDNOStored)
	}
}
