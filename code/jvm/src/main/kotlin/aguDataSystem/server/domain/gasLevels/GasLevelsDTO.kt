package aguDataSystem.server.domain.gasLevels

/**
 * Represents the dto for the gas levels of the AGU.
 *
 * @property min minimum level of the AGU in percentage
 * @property max maximum level of the AGU in percentage
 * @property critical critical level of the AGU in percentage
 */
data class GasLevelsDTO (
    val min: Int,
    val max: Int,
    val critical: Int
){
    fun toGasLevels() = GasLevels(
        min = this.min,
        max = this.max,
        critical = this.critical
    )
}