package aguDataSystem.server.repository

import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.RepositoryUtils.dummyDNO
import aguDataSystem.server.repository.RepositoryUtils.dummyEmergencyContact
import aguDataSystem.server.repository.RepositoryUtils.dummyLogisticContact
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.contact.JDBIContactRepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIContactRepositoryTest {

	@Test
	fun `add contact correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)

		// act
		val contactId = contactRepo.addContact(agu.cui, dummyLogisticContact)
		val contacts = contactRepo.getContactsByAGU(agu.cui)

		// assert
		assert(contacts.isNotEmpty())
		assert(contacts.any { it.id == contactId })
	}

	@Test
	fun `add contact with invalid number of digits should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			contactRepo.addContact(agu.cui, dummyLogisticContact.copy(phone = "12345"))
		}
	}

	@Test
	fun `add contact with invalid CUI should fail`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)

		// act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			contactRepo.addContact("INVALID_CUI", dummyLogisticContact)
		}
	}

	@Test
	fun `get contacts by AGU correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)
		val contactId1 = contactRepo.addContact(agu.cui, dummyLogisticContact)
		val contactId2 = contactRepo.addContact(agu.cui, dummyEmergencyContact)

		// act
		val contacts = contactRepo.getContactsByAGU(agu.cui)

		// assert
		assert(contacts.isNotEmpty())
		assert(contacts.any { it.id == contactId1 })
		assert(contacts.any { it.id == contactId2 })
	}

	@Test
	fun `get contacts by AGU with no contacts`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU.copy(contacts = emptyList())
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)

		// act
		val contacts = contactRepo.getContactsByAGU(agu.cui)

		// assert
		assert(contacts.isEmpty())
	}

	@Test
	fun `get contacts by AGU with invalid CUI`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)

		// act & assert
		assert(contactRepo.getContactsByAGU("INVALID_CUI").isEmpty())
	}

	@Test
	fun `delete contact correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)

		val contactId = contactRepo.addContact(agu.cui, dummyLogisticContact)

		val contactsBeforeDeletion = contactRepo.getContactsByAGU(agu.cui)
		require(contactsBeforeDeletion.isNotEmpty())
		require(contactsBeforeDeletion.any { it.id == contactId })

		// act
		contactRepo.deleteContact(agu.cui, contactId)

		val contacts = contactRepo.getContactsByAGU(agu.cui)

		// assert
		assert(contacts.isEmpty())
	}

	@Test
	fun `delete contact with invalid CUI does nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)

		val contactId = contactRepo.addContact(agu.cui, dummyLogisticContact)

		// act
		contactRepo.deleteContact("INVALID_CUI", contactId)

		val contacts = contactRepo.getContactsByAGU(agu.cui)

		// assert
		assert(contacts.isNotEmpty())
	}

	@Test
	fun `delete contact with invalid contact id does nothing`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)

		val contactId = contactRepo.addContact(agu.cui, dummyLogisticContact)

		// act
		contactRepo.deleteContact(agu.cui, Int.MIN_VALUE)
		val contacts = contactRepo.getContactsByAGU(agu.cui)

		// assert
		assertEquals(1, contacts.size)
		assert(contacts.any { it.id == contactId })
	}

	@Test
	fun `check if contact is stored correctly by id`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)
		val contactId = contactRepo.addContact(agu.cui, dummyLogisticContact)

		// act
		val isStored = contactRepo.isContactStoredById(agu.cui, contactId)

		// assert
		assert(isStored)
	}

	@Test
	fun `check if contact is stored by id with invalid CUI should return false`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val contactRepo = JDBIContactRepository(handle)
			val dnoRepo = JDBIDNORepository(handle)
			val aguRepo = JDBIAGURepository(handle)
			val dno = dummyDNO
			val agu = dummyAGU

			val dnoId = dnoRepo.addDNO(dno).id
			aguRepo.addAGU(agu, dnoId)

			val contactId = contactRepo.addContact(agu.cui, dummyLogisticContact)

			// act & assert
			assertFalse(contactRepo.isContactStoredById("INVALID_CUI", contactId))
		}

	@Test
	fun `check if contact is stored by phone number and type correctly`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = dummyDNO

		val dnoId = dnoRepo.addDNO(dno).id
		aguRepo.addAGU(agu, dnoId)
		contactRepo.addContact(agu.cui, dummyLogisticContact)

		// act
		val isStored = contactRepo.isContactStoredByPhoneNumberAndType(
			agu.cui,
			dummyLogisticContact.phone,
			dummyLogisticContact.type.name
		)

		// assert
		assert(isStored)
	}

	@Test
	fun `check if contact is stored with invalid CUI should return false`() = testWithHandleAndRollback { handle ->
		// arrange
		val contactRepo = JDBIContactRepository(handle)

		// act & assert
		assertFalse(
			contactRepo.isContactStoredByPhoneNumberAndType(
				"INVALID_CUI",
				dummyLogisticContact.phone,
				dummyLogisticContact.type.name
			)
		)
	}

	@Test
	fun `check if contact is stored with invalid contact details should return false`() =
		testWithHandleAndRollback { handle ->
			// arrange
			val contactRepo = JDBIContactRepository(handle)
			val aguRepo = JDBIAGURepository(handle)
			val dnoRepo = JDBIDNORepository(handle)

			val agu = dummyAGU
			val dno = dummyDNO

			val dnoId = dnoRepo.addDNO(dno).id
			aguRepo.addAGU(agu, dnoId)

			// act & assert
			assertFalse(
				contactRepo.isContactStoredByPhoneNumberAndType(
					agu.cui,
					phoneNumber = "INVALID_PHONE",
					dummyLogisticContact.type.name
				)
			)
		}
}
