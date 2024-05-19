package aguDataSystem.server.domain.gasLevels


/**
 * Represents the gas levels of the AGU.
 *
 * @property min minimum level of the AGU in percentage
 * @property max maximum level of the AGU in percentage
 * @property critical critical level of the AGU in percentage
 */
data class GasLevels(
	val min: Int,
	val max: Int,
	val critical: Int
)