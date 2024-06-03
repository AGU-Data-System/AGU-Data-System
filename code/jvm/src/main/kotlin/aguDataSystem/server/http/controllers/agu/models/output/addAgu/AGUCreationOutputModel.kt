package aguDataSystem.server.http.controllers.agu.models.output.addAgu

import aguDataSystem.server.domain.agu.AGU

/**
 * Represents the output model for creating an [AGU]
 *
 * @property cui The CUI of the [AGU]
 */
data class AGUCreationOutputModel(
	val cui: String
)
