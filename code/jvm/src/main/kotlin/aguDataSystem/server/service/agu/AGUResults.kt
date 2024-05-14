package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.server.service.errors.agu.GetMeasuresError
import aguDataSystem.server.service.errors.agu.UpdateAGUError
import aguDataSystem.utils.Either

/**
 * Result for creating an AGU
 */
typealias AGUCreationResult = Either<AGUCreationError, String>

/**
 * Result for getting an AGU
 */
typealias GetAGUResult = Either<GetAGUError, AGU>

/**
 * Result for getting temperature measures
 */
typealias GetTemperatureMeasuresResult = Either<GetMeasuresError, List<TemperatureMeasure>>

/**
 * Result for getting gas measures
 */
typealias GetGasMeasuresResult = Either<GetMeasuresError, List<GasMeasure>>

/**
 * Result for updating an AGU favourite state
 */
typealias UpdateFavouriteStateResult = Either<GetAGUError, Unit>

/**
 * Result for updating an AGU
 */
typealias UpdateAGUResult = Either<UpdateAGUError, Unit>
