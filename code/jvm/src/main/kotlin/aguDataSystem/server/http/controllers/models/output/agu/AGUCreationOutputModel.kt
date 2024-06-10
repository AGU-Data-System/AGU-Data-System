package aguDataSystem.server.http.controllers.models.output.agu

import aguDataSystem.server.domain.agu.AGU

/**
 * Represents the output model for creating an [AGU]
 *
 * @property cui The CUI of the [AGU]
 */
data class AGUCreationOutputModel(
	val cui: String
)
