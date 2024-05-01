package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.reading.Reading

/**
 * Represents a Provider
 *
 * @property id the id of the Provider
 * @property readings the readings of the Provider
 */
sealed class Provider {
	abstract val id: Int
	abstract val readings: List<Reading>

	/**
	 * Returns the last reading based on the timestamp
	 *
	 * @return The last reading
	 * @throws IllegalArgumentException if there are no readings
	 */
	fun getLatestReading(): Reading {
		return getLatestReadingOrNull() ?: throw IllegalArgumentException("No readings found")
	}

	/**
	 * Returns the last reading based on the timestamp
	 * or null if there are no readings
	 *
	 * @return The last reading or null
	 */
	private fun getLatestReadingOrNull(): Reading? {
		return readings.maxByOrNull { it.timestamp }
	}
}
