package aguDataSystem.server.http.controllers

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.media.Problem
import aguDataSystem.server.http.controllers.models.input.agu.AGUCreationInputModel
import aguDataSystem.server.http.controllers.models.input.agu.UpdateActiveAGUInputModel
import aguDataSystem.server.http.controllers.models.input.agu.UpdateFavouriteAGUInputModel
import aguDataSystem.server.http.controllers.models.input.gasLevels.GasLevelsInputModel
import aguDataSystem.server.http.controllers.models.input.notes.NotesInputModel
import aguDataSystem.server.http.controllers.models.output.agu.AGUBasicInfoListOutputModel
import aguDataSystem.server.http.controllers.models.output.agu.AGUCreationOutputModel
import aguDataSystem.server.http.controllers.models.output.agu.AGUOutputModel
import aguDataSystem.server.http.controllers.models.output.provider.GasMeasureListOutputModel
import aguDataSystem.server.http.controllers.models.output.provider.TemperatureMeasureListOutputModel
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.DeleteAGUError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.server.service.errors.agu.update.UpdateActiveStateError
import aguDataSystem.server.service.errors.agu.update.UpdateFavouriteStateError
import aguDataSystem.server.service.errors.agu.update.UpdateGasLevelsError
import aguDataSystem.server.service.errors.agu.update.UpdateNotesError
import aguDataSystem.server.service.errors.measure.GetMeasuresError
import aguDataSystem.utils.Failure
import aguDataSystem.utils.Success
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime

/**
 * Controller for the AGU operations
 */
@RestController("AGUs")
@RequestMapping(URIs.Agu.ROOT)
class AguController(private val service: AGUService) {

    /**
     * Gets all the AGUs
     *
     * @return a list of AGUs
     */
    @GetMapping
    fun getAll(): ResponseEntity<*> {
        val agus = service.getAGUsBasicInfo()
        return ResponseEntity.ok(AGUBasicInfoListOutputModel(agus))
    }

    /**
     * Get an AGU by ID
     *
     * @param aguCui the CUI of the AGU to search for
     * @return the AGU with the given ID
     */
    @GetMapping(URIs.Agu.BY_ID)
    fun getById(@PathVariable aguCui: String): ResponseEntity<*> {
        return when (val res = service.getAGUById(aguCui)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(AGUOutputModel(res.value))
        }
    }

    /**
     * Create a new AGU
     *
     * @param aguInput the AGU to create
     * @return the created AGU
     */
    @PostMapping(URIs.Agu.CREATE)
    fun create(@RequestBody aguInput: AGUCreationInputModel): ResponseEntity<*> {
        return when (val res = service.createAGU(aguInput.toAGUCreationDTO())) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.created(URIs.Agu.byID(res.value)).body(AGUCreationOutputModel(res.value))
        }
    }

    /**
     * Delete an AGU
     *
     * @param aguCui the CUI of the AGU to delete
     */
    @DeleteMapping(URIs.Agu.BY_ID)
    fun deleteAGU(@PathVariable aguCui: String): ResponseEntity<*> {
        return when (val res = service.deleteAGU(aguCui)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(res.value)
        }
    }

    /**
     * Get the prediction temperature measures of an AGU
     *
     * @param aguCui the CUI of the AGU to get the temperature measures from
     * @param days the number of days to get the measures from
     * @return the temperature measures of the AGU
     */
    @GetMapping(URIs.Agu.TEMPERATURE)
    fun getPredictionTemperatureMeasures(
        @PathVariable aguCui: String,
        @RequestParam(required = false, defaultValue = "10") days: Int
    ): ResponseEntity<*> {
        return when (val res = service.getPredictionTemperatureMeasures(aguCui, days)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(TemperatureMeasureListOutputModel(res.value))
        }
    }

    /**
     * Get the daily gas measures of an AGU
     *
     * @param aguCui the CUI of the AGU to get the gas measures from
     * @param days the number of days to get the measures from
     * @param time the time to get the measures for
     * @return the daily gas measures of the AGU
     */
    @GetMapping(URIs.Agu.DAILY_GAS_LEVELS)
    fun getDailyGasMeasures(
        @PathVariable aguCui: String,
        @RequestParam(required = false, defaultValue = "10") days: Int,
        @RequestParam(required = false, defaultValue = "09:00") time: LocalTime
    ): ResponseEntity<*> {
        return when (val res = service.getDailyGasMeasures(aguCui, days, time)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(GasMeasureListOutputModel(res.value))
        }
    }

    /**
     * Get the hourly gas measures of an AGU
     *
     * @param aguCui the CUI of the AGU to get the gas measures from
     * @param day the day to get the measures from
     * @return the hourly gas measures of the AGU
     */
    @GetMapping(URIs.Agu.HOURLY_GAS_LEVELS)
    fun getHourlyGasMeasures(
        @PathVariable aguCui: String,
        @RequestParam(required = true) day: LocalDate
    ): ResponseEntity<*> {
        return when (val res = service.getHourlyGasMeasures(aguCui, day)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(GasMeasureListOutputModel(res.value))
        }
    }

    /**
     * Get the prediction gas measures of an AGU
     *
     * @param aguCui the CUI of the AGU to get the gas measures from
     * @param days the number of days to get the measures from
     * @param time the time to get the measures for
     * @return the prediction gas measures of the AGU
     */
    @GetMapping(URIs.Agu.PREDICTION_GAS_LEVELS)
    fun getPredictionGasMeasures(
        @PathVariable aguCui: String,
        @RequestParam(required = false, defaultValue = "10") days: Int,
        @RequestParam(required = false, defaultValue = "09:00") time: LocalTime
    ): ResponseEntity<*> {
        return when (val res = service.getPredictionGasLevels(aguCui, days, time)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(GasMeasureListOutputModel(res.value))
        }
    }

    /**
     * Changes the favourite state of the AGU
     *
     * @param aguCui the CUI of the AGU to change
     * @param aguFavouriteInput the new favourite state of the AGU
     */
    @PutMapping(URIs.Agu.FAVOURITE_AGU)
    fun updateFavouriteState(
        @PathVariable aguCui: String,
        @RequestBody aguFavouriteInput: UpdateFavouriteAGUInputModel
    ): ResponseEntity<*> {
        return when (val res = service.updateFavouriteState(aguCui, aguFavouriteInput.isFavourite)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(AGUOutputModel(res.value))
        }
    }

    /**
     * Changes the active state of the AGU
     *
     * @param aguCui the CUI of the AGU to change
     * @param aguActiveInput the new active state of the AGU
     */
    @PutMapping(URIs.Agu.ACTIVE_AGU)
    fun updateActiveState(
        @PathVariable aguCui: String,
        @RequestBody aguActiveInput: UpdateActiveAGUInputModel
    ): ResponseEntity<*> {
        return when (val res = service.updateActiveState(aguCui, aguActiveInput.isActive)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(AGUOutputModel(res.value))
        }
    }

    /**
     * Changes the gas levels of an AGU
     *
     * @param aguCui the CUI of the AGU to change the gas levels of
     * @param gasLevels the new gas levels to change to
     * @return the AGU with the changed gas levels
     */
    @PutMapping(URIs.Agu.LEVELS)
    fun changeGasLevels(
        @PathVariable aguCui: String,
        @RequestBody gasLevels: GasLevelsInputModel
    ): ResponseEntity<*> {
        return when (val res = service.updateGasLevels(aguCui, gasLevels.toGasLevelsDTO())) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(AGUOutputModel(res.value))
        }
    }

    /**
     * Changes the notes of an AGU
     *
     * @param aguCui the CUI of the AGU to change the notes of
     * @param notesInputModel the new notes to change to
     * @return the AGU with the changed notes
     */
    @PutMapping(URIs.Agu.NOTES)
    fun changeNotes(
        @PathVariable aguCui: String,
        @RequestBody notesInputModel: NotesInputModel
    ): ResponseEntity<*> {
        return when (val res = service.updateNotes(aguCui, notesInputModel.notes)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(AGUOutputModel(res.value))
        }
    }

    /**
     * Train all AGUs
     */
    @PostMapping(URIs.Agu.TRAIN)
    fun trainAllAGUs() {
        service.trainAGUs()
    }

    /**
     * Resolve the problem of creating an AGU
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun AGUCreationError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            AGUCreationError.InvalidCUI -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCUI)
            AGUCreationError.InvalidContact -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidContact)
            AGUCreationError.InvalidContactType -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.InvalidContactType
            )

            AGUCreationError.InvalidCoordinates -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.InvalidCoordinates
            )

            AGUCreationError.InvalidCriticalLevel -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.InvalidCriticalLevel
            )

            AGUCreationError.InvalidLevels -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidLevels)
            AGUCreationError.InvalidLoadVolume -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.InvalidLoadVolume
            )

            AGUCreationError.InvalidMaxLevel -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.InvalidMaxLevel
            )

            AGUCreationError.InvalidMinLevel -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.InvalidMinLevel
            )

            AGUCreationError.InvalidTank -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidTank)
            AGUCreationError.ProviderError -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidProvider)
            AGUCreationError.InvalidName -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidName)
            AGUCreationError.AGUAlreadyExists -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.AGUAlreadyExists
            )

            AGUCreationError.AGUNameAlreadyExists -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.AGUNameAlreadyExists
            )

            AGUCreationError.TransportCompanyNotFound -> Problem.response(
                HttpStatus.NOT_FOUND.value(),
                Problem.TransportCompanyNotFound
            )

            AGUCreationError.DNONotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.DNONotFound)
            AGUCreationError.InvalidEIC -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidEIC)
        }

    /**
     * Resolve the problem of getting an AGU
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun GetAGUError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            GetAGUError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
            GetAGUError.InvalidCUI -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCUI)
        }

    /**
     * Resolve the problem of getting measures
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun GetMeasuresError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            GetMeasuresError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
            GetMeasuresError.ProviderNotFound -> Problem.response(
                HttpStatus.NOT_FOUND.value(),
                Problem.ProviderNotFound
            )

            GetMeasuresError.InvalidCUI -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCUI)
            GetMeasuresError.InvalidDays -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidDays)
            GetMeasuresError.InvalidTime -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidTime)
        }

    /**
     * Resolve the problem of updating the favourite state of an AGU
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun UpdateFavouriteStateError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            UpdateFavouriteStateError.AGUNotFound -> Problem.response(
                HttpStatus.NOT_FOUND.value(),
                Problem.AGUNotFound
            )
        }

    /**
     * Resolve the problem of updating the active state of an AGU
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun UpdateActiveStateError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            UpdateActiveStateError.AGUNotFound -> Problem.response(
                HttpStatus.NOT_FOUND.value(),
                Problem.AGUNotFound
            )
        }

    /**
     * Resolves the problem of updating the gas levels of an AGU
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun UpdateGasLevelsError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            UpdateGasLevelsError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
            UpdateGasLevelsError.InvalidLevels -> Problem.response(
                HttpStatus.BAD_REQUEST.value(),
                Problem.InvalidLevels
            )
        }

    /**
     * Resolves the problem of updating the notes of an AGU
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun UpdateNotesError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            UpdateNotesError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
        }

    /**
     * Resolves the problem of deleting an AGU
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun DeleteAGUError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            DeleteAGUError.InvalidCUI -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCUI)
        }
}
