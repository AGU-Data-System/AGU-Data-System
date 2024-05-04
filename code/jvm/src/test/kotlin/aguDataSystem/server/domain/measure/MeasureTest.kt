package aguDataSystem.server.domain.measure

import aguDataSystem.server.domain.provider.toProviderType
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MeasureTest {

	@Test
	fun `buildReading should return a valid gas Reading`() {
		// arrange
		val sut = "gas".toProviderType()
		val curTime = LocalDateTime.now()
		val predictionTime = LocalDateTime.now().plusDays(1)
		val value = 10

		// act
		val reading = sut.buildMeasure(
			timestamp = curTime,
			predictionFor = predictionTime,
			values = intArrayOf(value)
		)

		// assert
		assert(reading is GasMeasure)
	}

	@Test
	fun `buildReading should return a valid temperature Reading`() {
		// arrange
		val sut = "temperature".toProviderType()
		val curTime = LocalDateTime.now()
		val predictionTime = LocalDateTime.now().plusDays(1)
		val min = 10
		val max = 20

		// act
		val reading = sut.buildMeasure(
			timestamp = curTime,
			predictionFor = predictionTime,
			values = intArrayOf(min, max)
		)

		// assert
		assert(reading is TemperatureMeasure)
	}

	@Test
	fun `buildReading should throw an exception when the number of values is invalid`() {
		// arrange
		val sut = "gas".toProviderType()
		val curTime = LocalDateTime.now()
		val predictionTime = LocalDateTime.now().plusDays(1)
		val invalidValues = intArrayOf(10, 20, 30)

		// act & assert
		assertFailsWith<IllegalArgumentException> {
			sut.buildMeasure(
				timestamp = curTime,
				predictionFor = predictionTime,
				values = invalidValues
			)
		}
	}
}