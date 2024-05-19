package aguDataSystem.server.domain.contact

import kotlin.test.Test
import kotlin.test.assertFailsWith

class ContactTest {

	private val dummyContactCreationDTO = ContactCreationDTO(
		name = "John Doe",
		phone = "123456789",
		type = "emergency"
	)

	@Test
	fun `toContact should return a valid Contact`() {
		// arrange
		val sut = dummyContactCreationDTO

		// act
		val contact = sut.toContactCreation()

		// assert
		assert(contact.name == "John Doe")
		assert(contact.phone == "123456789")
		assert(contact.type == ContactType.EMERGENCY)
	}

	@Test
	fun `toContact should throw an exception when the type is invalid`() {
		// arrange
		val sut = dummyContactCreationDTO.copy(type = "invalid")

		// act & assert
		assertFailsWith<IllegalArgumentException> {
			sut.toContactCreation()
		}
	}
}