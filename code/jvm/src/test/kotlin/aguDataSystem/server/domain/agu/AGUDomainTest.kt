package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.GasLevels
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AGUDomainTest {

	private val aguDomain = AGUDomain()

	@Test
	fun `isCUIValid should validate CUI correctly`() {
		val validCUI = "PT1234567890123456AB"
		val invalidCUI = "PT1234567890123456A"
		assertTrue(aguDomain.isCUIValid(validCUI))
		assertFalse(aguDomain.isCUIValid(invalidCUI))
	}

	@Test
	fun `isPhoneValid should validate phone number correctly`() {
		val validPhone = "123456789"
		val invalidPhone = "12345678"
		assertTrue(aguDomain.isPhoneValid(validPhone))
		assertFalse(aguDomain.isPhoneValid(invalidPhone))
	}

	@Test
	fun `isContactTypeValid should validate contact type correctly`() {
		val validContactType = "LOGISTIC"
		val invalidContactType = "INVALID"
		assertTrue(aguDomain.isContactTypeValid(validContactType))
		assertFalse(aguDomain.isContactTypeValid(invalidContactType))
	}

	@Test
	fun `isPercentageValid should validate percentage correctly`() {
		val validPercentage = 50
		val invalidPercentage = 150
		assertTrue(aguDomain.isPercentageValid(validPercentage))
		assertFalse(aguDomain.isPercentageValid(invalidPercentage))
	}

	@Test
	fun `areCoordinatesValid should validate latitude correctly`() {
		val validLatitude = 50.0
		val invalidLatitude = 100.0
		val validLongitude = 50.0
		val invalidLongitude = 200.0
		assertTrue(aguDomain.areCoordinatesValid(validLatitude, validLongitude))
		assertFalse(aguDomain.areCoordinatesValid(invalidLatitude, validLongitude))
		assertFalse(aguDomain.areCoordinatesValid(validLatitude, invalidLongitude))
		assertFalse(aguDomain.areCoordinatesValid(invalidLatitude, invalidLongitude))
	}

	@Test
	fun `areLevelsValid should validate levels correctly`() {
		val validLevels = GasLevels(1, 2, 1)
		val invalidLevels = GasLevels(1, 2, 3)
		assertTrue(aguDomain.areLevelsValid(validLevels))
		assertFalse(aguDomain.areLevelsValid(invalidLevels))
	}
}