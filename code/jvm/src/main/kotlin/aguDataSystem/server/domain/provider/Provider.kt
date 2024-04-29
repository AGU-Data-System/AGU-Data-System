package aguDataSystem.server.domain.provider

import aguDataSystem.server.domain.reading.Reading

/**
 * Represents a Provider
 */
sealed class Provider {
	abstract val id: Int
	abstract val readings: List<Reading>

	/**
	 * Returns the last reading
	 */
	fun getLatestReading(): Reading {
		return getLatestReadingOrNull() ?: throw IllegalArgumentException("No readings found")
		// TODO: find a way to take out the exception on the case of no readings is empty
	}

	/**
	 * Returns the last reading or null
	 */
	private fun getLatestReadingOrNull(): Reading? {
		return readings.maxByOrNull { it.timestamp }
	}
}
