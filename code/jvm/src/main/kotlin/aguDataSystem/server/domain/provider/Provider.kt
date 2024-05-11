package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.measure.Measure
import java.time.LocalDateTime

/**
 * Represents a Provider
 *
 * @property id the id of the Provider
 * @property measures the readings of the Provider
 */
sealed class Provider {
	abstract val id: Int
	open val measures: List<Measure> = emptyList()
	abstract val lastFetch: LocalDateTime?

	/**
	 * Returns the last reading based on the timestamp
	 *
	 * @return The last reading
	 * @throws IllegalArgumentException if there are no readings
	 */
	fun getLatestReading(): Measure {
		return getLatestReadingsOrEmpty().firstOrNull() ?: throw IllegalArgumentException("No readings found")
	}

	/**
	 * Returns the last reading based on the timestamp
	 * or null if there are no readings
	 *
	 * @return The last reading or null
	 */
	private fun getLatestReadingsOrEmpty(): List<Measure> {
		return this.measures.filter { it.timestamp == this.lastFetch }
	}
}
