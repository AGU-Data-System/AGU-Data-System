package aguDataSystem.server.domain

/**
 * Represents a tank
 *
 * @property number The number of the tank
 * @property levels The gas levels of the tank
 * @property loadVolume The load volume of the tank
 * @property capacity The capacity of the tank
 */
data class Tank(
	val number: Int,
	val levels: GasLevels,
	val loadVolume: Int,
	val capacity: Int,
) {
	init {
		require(number > 0) { "Number must be greater than 0" } // TODO change to initial index
		require(AGUDomain().isPercentageValid(loadVolume)) { "Load volume is not valid" }
		require(AGUDomain().isPercentageValid(capacity)) { "Capacity is not valid" }
	}
}