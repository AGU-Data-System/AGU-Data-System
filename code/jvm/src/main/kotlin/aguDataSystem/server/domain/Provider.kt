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
	fun getLastReading(): Reading {
		return readings.last()
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
 * Creates a Provider based on a type.
 * @param id The id of the Provider.
 * @param readings The readings of the Provider.
 * @param type The type of the Provider.
 * @return The created Provider.
 * @throws IllegalArgumentException If the type is invalid.
 */
fun createProvider(id: Int, readings: List<Reading>, type: String): Provider {
	return when(type) {
		"GAS" -> GasProvider(id, readings)
		"TEMPERATURE" -> TemperatureProvider(id, readings)
		else -> throw IllegalArgumentException("Invalid provider type")
	}
}