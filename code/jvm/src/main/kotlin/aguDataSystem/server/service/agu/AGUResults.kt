package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.utils.Either

/**
 * Result for creating an AGU
 */
typealias AGUCreationResult = Either<AGUCreationError, String>

/**
 * Result for getting an AGU
 */
typealias GetAGUResult = Either<GetAGUError, AGU>