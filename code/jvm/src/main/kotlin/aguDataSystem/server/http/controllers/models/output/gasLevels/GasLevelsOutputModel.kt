package aguDataSystem.server.http.controllers.models.output.gasLevels

import aguDataSystem.server.domain.gasLevels.GasLevels

/**
 * Output model for GasLevels
 *
 * @param min The minimum gas level
 * @param max The maximum gas level
 * @param critical The critical gas level
 */
data class GasLevelsOutputModel(
	val min: Int,
	val max: Int,
	val critical: Int
) {
	constructor(gasLevels: GasLevels) : this(
		min = gasLevels.min,
		max = gasLevels.max,
		critical = gasLevels.critical
	)
}