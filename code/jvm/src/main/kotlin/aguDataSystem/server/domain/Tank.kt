package aguDataSystem.server.domain

/**
 * Represents a tank.
 *
 * @property number The number of the tank.
 * @property minLevel The minimum level of the tank.
 * @property maxLevel The maximum level of the tank.
 * @property criticalLevel The critical level of the tank.
 * @property loadVolume The load volume of the tank.
 * @property capacity The capacity of the tank.
 */
data class Tank(
	val number : Int,
	val minLevel : Int,
	val maxLevel : Int,
	val criticalLevel : Int,
	val loadVolume : Double,
	val capacity : Double,
)