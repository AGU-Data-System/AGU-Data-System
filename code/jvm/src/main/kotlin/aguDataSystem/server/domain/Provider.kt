package aguDataSystem.server.domain

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

/**
 * Represents a Gas Provider
 *
 * @property id the id of the Gas Provider
 * @property readings the readings of the Gas Provider
 */
data class GasProvider(
	override val id: Int,
	override val readings: List<Reading>
) : Provider()

/**
 * Represents a temperature Provider
 *
 * @property id the id of the temperature Provider
 * @property readings the readings of the temperature Provider
 */
data class TemperatureProvider(
	override val id: Int,
	override val readings: List<Reading>
) : Provider()

/**
 * Creates a Provider based on a type
 *
 * @receiver The type of the Provider
 * @param id The id of the Provider
 * @param readings The readings of the Provider
 * @return The created Provider
 * @throws IllegalArgumentException If the type is invalid
 */
fun String.createProvider(id: Int, readings: List<Reading>): Provider {
	return when (this.uppercase()) {
		"GAS" -> GasProvider(id = id, readings = readings)
		"TEMPERATURE" -> TemperatureProvider(id = id, readings = readings)
		else -> throw IllegalArgumentException("Invalid provider type")
	}
}