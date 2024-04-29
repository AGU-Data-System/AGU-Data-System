package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.utils.Either

/**
 * Result for creating an AGU
 */
typealias AGUCreationResult = Either<AGUCreationError, AGU>
