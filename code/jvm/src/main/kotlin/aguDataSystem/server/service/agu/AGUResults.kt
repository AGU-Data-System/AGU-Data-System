package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.http.controllers.agu.models.addAgu.AGUCreationOutputModel
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.server.service.errors.agu.update.UpdateFavouriteStateError
import aguDataSystem.server.service.errors.agu.update.UpdateGasLevelsError
import aguDataSystem.server.service.errors.agu.update.UpdateNotesError
import aguDataSystem.server.service.errors.contact.AddContactError
import aguDataSystem.server.service.errors.contact.DeleteContactError
import aguDataSystem.server.service.errors.measure.GetMeasuresError
import aguDataSystem.server.service.errors.tank.AddTankError
import aguDataSystem.server.service.errors.tank.UpdateTankError
import aguDataSystem.utils.Either

/**
 * Result for creating an AGU
 */
typealias AGUCreationResult = Either<AGUCreationError, AGUCreationOutputModel>

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
typealias UpdateFavouriteStateResult = Either<UpdateFavouriteStateError, AGU>

/**
 * Result for adding a contact to an AGU
 */
typealias AddContactResult = Either<AddContactError, Int>

/**
 * Result for deleting a contact from an AGU
 */
typealias DeleteContactResult = Either<DeleteContactError, Unit>

/**
 * Result for adding a tank to an AGU
 */
typealias AddTankResult = Either<AddTankError, Int>

/**
 * Result for updating a tank from an AGU
 */
typealias UpdateTankResult = Either<UpdateTankError, AGU>

/**
 * Result for updating the gas levels of an AGU
 */
typealias UpdateGasLevelsResult = Either<UpdateGasLevelsError, AGU>

/**
 * Result for updating the notes of an AGU
 */
typealias UpdateNotesResult = Either<UpdateNotesError, AGU>
