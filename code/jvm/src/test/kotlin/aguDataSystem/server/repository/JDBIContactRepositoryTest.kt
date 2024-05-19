package aguDataSystem.server.repository

import aguDataSystem.server.domain.contact.ContactType
import aguDataSystem.server.repository.RepositoryUtils.DUMMY_DNO_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
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

	private val dummyContact = dummyLogisticContact.copy(type = ContactType.EMERGENCY)

	@Test
	fun `add contact correctly`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)
		//act
		contactRepo.addContact(agu.cui, dummyContact)
		val contacts = contactRepo.getContactsByAGU(agu.cui)

		//assert
		assert(contacts.isNotEmpty())
		assert(contacts.any { it.phone == dummyContact.phone && it.name == dummyContact.name  && it.type == dummyContact.type })
	}

	@Test
	fun `add contact with invalid number of digits should fail`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)
		//act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			contactRepo.addContact(agu.cui, dummyContact.copy(phone = ""))
		}
	}

	@Test
	fun `add contact with invalid CUI should fail`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		//act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			contactRepo.addContact("", dummyContact)
		}
	}

	@Test
	fun `get contacts by AGU correctly`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)
		contactRepo.addContact(agu.cui, dummyLogisticContact)
		contactRepo.addContact(agu.cui, dummyEmergencyContact)

		//act
		val contacts = contactRepo.getContactsByAGU(agu.cui)

		//assert
		assert(contacts.isNotEmpty())
		assert(contacts.any { it.phone == dummyLogisticContact.phone && it.name == dummyLogisticContact.name && it.type == dummyLogisticContact.type })
		assert(contacts.any { it.phone == dummyEmergencyContact.phone && it.name == dummyEmergencyContact.name && it.type == dummyEmergencyContact.type })
	}

	@Test
	fun `get contacts by AGU with no contacts`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU.copy(contacts = emptyList())
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)
		//act
		val contacts = contactRepo.getContactsByAGU(agu.cui)

		//assert
		assert(contacts.isEmpty())
	}

	@Test
	fun `get contacts by AGU with invalid CUI`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		//act & assert
		assert(contactRepo.getContactsByAGU("").isEmpty())
	}

	@Test
	fun `delete contact correctly`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)

		contactRepo.addContact(agu.cui, dummyContact)

		val contactsBeforeDeletion = contactRepo.getContactsByAGU(agu.cui)
		require(contactsBeforeDeletion.isNotEmpty())
		//act

		contactRepo.deleteContact(agu.cui, contactsBeforeDeletion.first { it.phone == dummyContact.phone }.id)

		val contacts = contactRepo.getContactsByAGU(agu.cui)

		//assert
		assert(contacts.isEmpty())
	}

	@Test
	fun `delete contact with invalid CUI does nothing`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)

		val contactId = contactRepo.addContact(agu.cui, dummyContact)

		//act
		contactRepo.deleteContact("", contactId)

		val contacts = contactRepo.getContactsByAGU(agu.cui)

		//assert
		assert(contacts.isNotEmpty())
	}

	@Test
	fun `delete contact with invalid contact id`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)

		agu.contacts.forEach {
			contactRepo.addContact(agu.cui, it)
		}

		//act
		val contactsBefore = contactRepo.getContactsByAGU(agu.cui)
		contactRepo.deleteContact(agu.cui, Int.MIN_VALUE)
		val contactsAfter = contactRepo.getContactsByAGU(agu.cui)

		//assert
		assert(contactsBefore.containsAll(contactsAfter))
		assertEquals(contactsBefore.size, contactsAfter.size)
	}

	@Test
	fun `check if contact is stored correctly`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)
		val contactId = contactRepo.addContact(agu.cui, dummyLogisticContact)

		//act
		val isStored = contactRepo.isContactStoredById(agu.cui, contactId)

		//assert
		assert(isStored)
	}

	@Test
	fun `check if contact is stored with invalid CUI`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		//act & assert
		assertFalse(contactRepo.isContactStoredByPhoneNumberAndType("", dummyLogisticContact.phone, dummyLogisticContact.type.name))
	}

	@Test
	fun `check if contact is stored with invalid contact`() = testWithHandleAndRollback { handle ->
		//arrange
		val contactRepo = JDBIContactRepository(handle)
		val aguRepo = JDBIAGURepository(handle)
		val dnoRepo = JDBIDNORepository(handle)

		val agu = dummyAGU
		val dno = DUMMY_DNO_NAME

		val dnoId = dnoRepo.addDNO(dno)
		aguRepo.addAGU(agu, dnoId)

		//act & assert
		assertFalse(contactRepo.isContactStoredByPhoneNumberAndType(agu.cui, phoneNumber = "", dummyLogisticContact.type.name))
	}
}
