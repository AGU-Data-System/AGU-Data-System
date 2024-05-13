package aguDataSystem.server.http.controllers.agu

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.agu.models.addAgu.AGUCreationInputModel
import aguDataSystem.server.http.controllers.media.Problem
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.server.service.errors.agu.GetMeasuresError
import aguDataSystem.utils.Failure
import aguDataSystem.utils.Success
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalTime

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
        return ResponseEntity.ok(service.getAGUsBasicInfo())
    }

    /**
     * Get an AGU by ID
     *
     * @param aguCui the CUI of the AGU to search for
     * @return the AGU with the given ID
     */
    @GetMapping(URIs.Agu.GET_BY_ID)
    fun getById(@PathVariable aguCui: String): ResponseEntity<*> {
        return when (val res = service.getAGUById(aguCui)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(res.value)
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
            is Success -> ResponseEntity.created(URIs.Agu.byID(res.value)).body(res.value)
        }
    }

    /**
     * Get the temperature measures of an AGU
     *
     * @param aguCui the CUI of the AGU to get the temperature measures from
     * @param days the number of days to get the measures from
     * @return the temperature measures of the AGU
     */
    @GetMapping(URIs.Agu.GET_TEMPERATURE)
    fun getTemperatureMeasures(
        @PathVariable aguCui: String,
        @RequestParam(required = false, defaultValue = "10") days: Int
    ): ResponseEntity<*> {
        return when (val res = service.getTemperatureMeasures(aguCui, days)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(res.value)
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
    @GetMapping(URIs.Agu.GET_DAILY_GAS_LEVELS)
    fun getDailyGasMeasures(
        @PathVariable aguCui: String,
        @RequestParam(required = false, defaultValue = "10") days: Int,
        @RequestParam(
            required = false,
            defaultValue = "09:00"
        ) time: LocalTime //todo: maybe don't put default values, and if not provided, put the default value in the service
    ): ResponseEntity<*> {
        return when (val res = service.getDailyGasMeasures(aguCui, days, time)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(res.value)
        }
    }

    /**
     * Get the hourly gas measures of an AGU
     *
     * @param aguCui the CUI of the AGU to get the gas measures from
     * @param day the day to get the measures from
     * @return the hourly gas measures of the AGU
     */
    @GetMapping(URIs.Agu.GET_HOURLY_GAS_LEVELS)
    fun getHourlyGasMeasures(
        @PathVariable aguCui: String,
        @RequestParam(required = true) day: LocalDate
    ): ResponseEntity<*> {
        return when (val res = service.getHourlyGasMeasures(aguCui, day)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(res.value)
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
    @GetMapping(URIs.Agu.GET_PREDICTION_GAS_LEVELS)
    fun getPredictionGasMeasures(
        @PathVariable aguCui: String,
        @RequestParam(required = false, defaultValue = "10") days: Int,
        @RequestParam(required = false, defaultValue = "09:00") time: LocalTime
    ): ResponseEntity<*> {
        return when (val res = service.getPredictionGasLevels(aguCui, days, time)) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.ok(res.value)
        }
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

            AGUCreationError.InvalidDNO -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidDNO)
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
            AGUCreationError.ProviderError -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.ProviderError)
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
        }
}