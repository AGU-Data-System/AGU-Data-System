package aguDataSystem.server.http.controllers.agu

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.agu.models.addAgu.AGUCreationInputModel
import aguDataSystem.server.http.controllers.media.Problem
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.utils.Failure
import aguDataSystem.utils.Success
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("AGUs")
@RequestMapping(URIs.Agu.ROOT)
class AguController(private val service: AGUService) {

	/**
	 * Get an AGU by ID
	 *
	 * @param aguId the ID of the AGU to get
	 * @return the AGU with the given ID
	 */
	@GetMapping(URIs.Agu.GET_BY_ID)
	fun getById(aguId: Int) {
		//TODO
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
			is Success -> ResponseEntity.created(URIs.Agu.byID(res.value.cui)).body(res.value)
		}
	}

	/**
	 * Resolve the problem of creating an AGU
	 *
	 * @receiver the error to resolve
	 * @return the response entity to return
	 */
	private fun AGUCreationError.resolveProblem(): ResponseEntity<*> =
        when(this) {
            AGUCreationError.InvalidCUI -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCUI)
            AGUCreationError.InvalidContact -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidContact)
            AGUCreationError.InvalidContactType -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidContactType)
            AGUCreationError.InvalidCoordinates -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCoordinates)
            AGUCreationError.InvalidCriticalLevel -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCriticalLevel)
            AGUCreationError.InvalidDNO -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidDNO)
            AGUCreationError.InvalidLevels -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidLevels)
            AGUCreationError.InvalidLoadVolume -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidLoadVolume)
            AGUCreationError.InvalidMaxLevel -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidMaxLevel)
            AGUCreationError.InvalidMinLevel -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidMinLevel)
            AGUCreationError.InvalidTank -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidTank)
        }
}