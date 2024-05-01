package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.reading.GasReading
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class ProviderTest {

	@Test
	fun `toProviderType should return a valid ProviderType`() {
		// arrange
		val sut = "gas"

		// act
		val providerType = sut.toProviderType()

		// assert
		assert(providerType == ProviderType.GAS)
	}

	@Test
	fun `toProviderType should throw an exception when the type is invalid`() {
		// arrange
		val sut = "invalid"

		// act & assert
		assertFailsWith<IllegalArgumentException> {
			sut.toProviderType()
		}
	}

	@Test
	fun `createProviderWithReadings should return a valid Provider`() {
		// arrange
		val sut = ProviderType.GAS
		val readings = listOf(
			GasReading(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10),
			GasReading(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 20)
		)

		// act
		val provider = sut.createProviderWithReadings(id = 1, readings = readings)

		// assert
		assertEquals(provider.readings, readings)
		assertTrue(provider is GasProvider)
		provider.readings.forEach { assertTrue(it is GasReading) }
	}

	@Test
	fun `createProviderWithReadings should throw an exception when the type doesn't match Readings`() {
		// arrange
		val sut = ProviderType.TEMPERATURE
		val readings = listOf(
			GasReading(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10),
			GasReading(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 20)
		)

		// act & assert
		assertFailsWith<IllegalArgumentException> {
			sut.createProviderWithReadings(id = 1, readings = readings)
		}
	}
}