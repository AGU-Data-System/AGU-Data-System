package aguDataSystem.server.domain

/**
 * Represents the gas levels of a tank or AGU.
 *
 * @property min The minimum gas level.
 * @property max The maximum gas level.
 * @property critical The critical gas level.
 */
data class GasLevels(
	val min: Int,
	val max: Int,
	val critical: Int,
) {
	init {
		require(min in 0..100) { "Min must be between 0 and 100" }
		require(max in 0..100) { "Max must be between 0 and 100" }
		require(critical in 0..100) { "Critical must be between 0 and 100" }
		require(min < max) { "Min must be less than max" }
		require(min in critical..max) { "Min must be between critical and max" }
	}
}
