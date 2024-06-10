package aguDataSystem.server.http.controllers.models.input.agu

import aguDataSystem.server.domain.agu.AGU

/**
 * Represents the input model for updating the active status of an [AGU]
 *
 * @property isActive The new active status of the AGU
 */
data class UpdateActiveAGUInputModel(
	val isActive: Boolean
)