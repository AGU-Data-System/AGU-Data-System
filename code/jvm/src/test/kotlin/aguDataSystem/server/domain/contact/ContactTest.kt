package aguDataSystem.server.domain.contact

import kotlin.test.Test
import kotlin.test.assertFailsWith

class ContactTest {

	private val dummyContactDTO = ContactDTO(
		name = "John Doe",
		phone = "123456789",
		type = "emergency"
	)

	@Test
	fun `toContact should return a valid Contact`() {
		// arrange
		val sut = dummyContactDTO

		// act
		val contact = sut.toContact()

		// assert
		assert(contact.name == "John Doe")
		assert(contact.phone == "123456789")
		assert(contact.type == ContactType.EMERGENCY)
	}

	@Test
	fun `toContact should throw an exception when the type is invalid`() {
		// arrange
		val sut = dummyContactDTO.copy(type = "invalid")

		// act & assert
		assertFailsWith<IllegalArgumentException> {
			sut.toContact()
		}
	}
}