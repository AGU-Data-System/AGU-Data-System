package aguDataSystem.server.domain

/**
 * Represents the gas levels of the AGU.
 *
 * @property min minimum level of the AGU
 * @property max maximum level of the AGU
 * @property critical critical level of the AGU
 */
data class GasLevels(
	val min: Int,
	val max: Int,
	val critical: Int
)