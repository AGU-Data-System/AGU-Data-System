package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.provider.ProviderInput
import aguDataSystem.utils.getSuccessOrThrow
import aguDataSystem.utils.isFailure
import aguDataSystem.utils.isSuccess
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AGUDomainTest {

	private val aguDomain = AGUDomain()
	private val dummyProviderInput = ProviderInput(
		name = "Provider AGU Domain Test",
		url = "https://jsonplaceholder.typicode.com/todos/1",
		frequency = "PT1H",
		isActive = true
	)

	@Test
	fun `isCUIValid should validate CUI correctly`() {
		//arrange
		val validCUI = "PT1234567890123456AB"
		val invalidCUI = "PT1234567890123456A"
		val invalidCUI2 = ""
		val invalidCUI3 = "PT1234567890123456ABC"

		// act & assert
		assertTrue(aguDomain.isCUIValid(validCUI))
		assertFalse(aguDomain.isCUIValid(invalidCUI))
		assertFalse(aguDomain.isCUIValid(invalidCUI2))
		assertFalse(aguDomain.isCUIValid(invalidCUI3))
	}

	@Test
	fun `isPhoneValid should validate phone number correctly`() {
		// arrange
		val validPhone = "123456789"
		val invalidPhone = "12345678"
		val invalidPhone2 = "1234567890"
		val invalidPhone3 = ""

		// act & assert
		assertTrue(aguDomain.isPhoneValid(validPhone))
		assertFalse(aguDomain.isPhoneValid(invalidPhone))
		assertFalse(aguDomain.isPhoneValid(invalidPhone2))
		assertFalse(aguDomain.isPhoneValid(invalidPhone3))
	}

	@Test
	fun `isContactTypeValid should validate contact type correctly`() {
		// arrange
		val validContactType = "LOGISTIC"
		val validContactType2 = "logistic"
		val invalidContactType = "INVALID"
		val invalidContactType2 = ""

		// act & assert
		assertTrue(aguDomain.isContactTypeValid(validContactType))
		assertTrue(aguDomain.isContactTypeValid(validContactType2))
		assertFalse(aguDomain.isContactTypeValid(invalidContactType))
		assertFalse(aguDomain.isContactTypeValid(invalidContactType2))
	}

	@Test
	fun `isPercentageValid should validate percentage correctly`() {
		// arrange
		val validPercentage = 50
		val invalidPercentage = 150
		val invalidPercentage2 = -1

		// act & assert
		assertTrue(aguDomain.isPercentageValid(validPercentage))
		assertFalse(aguDomain.isPercentageValid(invalidPercentage))
		assertFalse(aguDomain.isPercentageValid(invalidPercentage2))
	}

	@Test
	fun `areCoordinatesValid should validate latitude correctly`() {
		// arrange
		val validLatitude = 50.0
		val invalidLatitude = 100.0
		val invalidLatitude2 = -100.0
		val validLongitude = 50.0
		val invalidLongitude = 200.0
		val invalidLongitude2 = -200.0

		// act & assert
		assertTrue(aguDomain.areCoordinatesValid(validLatitude, validLongitude))
		assertFalse(aguDomain.areCoordinatesValid(invalidLatitude, validLongitude))
		assertFalse(aguDomain.areCoordinatesValid(validLatitude, invalidLongitude))
		assertFalse(aguDomain.areCoordinatesValid(invalidLatitude, invalidLongitude))
		assertFalse(aguDomain.areCoordinatesValid(validLatitude, invalidLongitude2))
		assertFalse(aguDomain.areCoordinatesValid(invalidLatitude2, validLongitude))
	}

	@Test
	fun `areLevelsValid should validate levels correctly`() {
		// arrange
		val validLevels = GasLevels(min = 1, max = 2, critical = 1)
		val validLevels2 = GasLevels(min = 1, max = 2, critical = 0)
		val invalidLevels = GasLevels(min = 1, max = 2, critical = 3)
		val invalidLevels2 = GasLevels(min = 1, max = 0, critical = 1)
		val invalidLevels3 = GasLevels(min = 0, max = 1, critical = -1)
		val invalidLevels4 = GasLevels(min = 10, max = 101, critical = 1)

		// act & assert
		assertTrue(aguDomain.areLevelsValid(validLevels))
		assertTrue(aguDomain.areLevelsValid(validLevels2))
		assertFalse(aguDomain.areLevelsValid(invalidLevels))
		assertFalse(aguDomain.areLevelsValid(invalidLevels2))
		assertFalse(aguDomain.areLevelsValid(invalidLevels3))
		assertFalse(aguDomain.areLevelsValid(invalidLevels4))
	}

	@Test
	fun `add provider request properly`() {
		// arrange
		val provider = dummyProviderInput

		// act
		val result = aguDomain.addProviderRequest(provider)

		// assert
		assertTrue(result.isSuccess())

		// clean
		val providerId = result.getSuccessOrThrow()
		aguDomain.deleteProviderRequest(providerId)
	}

	@Test
	fun `add provider request should fail with invalid url`() {
		// arrange
		val provider = dummyProviderInput.copy(url = "invalid url")

		// act
		val result = aguDomain.addProviderRequest(provider)

		// assert
		assertTrue(result.isFailure())
	}

	@Test
	fun `add provider request should fail with invalid frequency`() {
		// arrange
		val provider = dummyProviderInput.copy(frequency = "invalid frequency")

		// act
		val result = aguDomain.addProviderRequest(provider)

		// assert
		assertTrue(result.isFailure())
	}

	@Test
	fun `delete provider request properly`() {
		// arrange
		val provider = dummyProviderInput
		val addProviderResult = aguDomain.addProviderRequest(provider)
		val providerId = addProviderResult.getSuccessOrThrow()

		// act
		val result = aguDomain.deleteProviderRequest(providerId)

		// assert
		assertTrue(result.isSuccess())
	}

	@Test
	fun `delete provider request should fail with invalid providerID`() {
		// arrange
		val providerId = -1 // For the love of god DO NOT, I REPEAT, DO NOT CHANGE THIS ID TO A VALID ONE // SPF

		// act
		val result = aguDomain.deleteProviderRequest(providerId)

		// assert
		assertTrue(result.isSuccess()) // This is a success because the provider with the given ID does not exist
	}
}