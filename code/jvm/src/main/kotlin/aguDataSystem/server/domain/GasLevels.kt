package aguDataSystem.server.domain


/**
 * Represents the gas levels of the AGU.
 *
 * @property min minimum level of the AGU in percentage
 * @property max maximum level of the AGU in percentage
 * @property critical critical level of the AGU in percentage
 */
data class GasLevels(
	val min: Int, //Percentage,
	val max: Int, //Percentage,
	val critical: Int //Percentage
)