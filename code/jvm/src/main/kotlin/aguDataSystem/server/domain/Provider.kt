package aguDataSystem.server.domain

/**
 * Represents a Provider
 */
sealed class Provider {
	abstract val id: Int
	abstract val readings: List<Reading>
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
 * Represents a weather Provider
 *
 * @property id the id of the weather Provider
 * @property readings the readings of the weather Provider
 */
data class WeatherProvider(
	override val id: Int,
	override val readings: List<Reading>
) : Provider()
