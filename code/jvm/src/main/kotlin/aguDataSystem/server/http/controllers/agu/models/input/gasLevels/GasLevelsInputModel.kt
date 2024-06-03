package aguDataSystem.server.http.controllers.agu.models.input.gasLevels

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.gasLevels.GasLevelsDTO

/**
 * Represents the input model for updating the gas levels of an [AGU]
 *
 * @property min The minimum gas level
 * @property max The maximum gas level
 * @property critical The critical gas level
 */
data class GasLevelsInputModel(
	val min: Int,
	val max: Int,
	val critical: Int
) {

	/**
	 * Converts the input model to a [GasLevelsDTO]
	 *
	 * @receiver The input model to convert
	 * @return The [GasLevelsDTO] representation of the input model
	 */
	fun toGasLevelsDTO() = GasLevelsDTO(
		min = this.min,
		max = this.max,
		critical = this.critical
	)
}