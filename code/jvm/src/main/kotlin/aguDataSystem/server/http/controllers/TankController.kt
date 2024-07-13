package aguDataSystem.server.http.controllers

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.media.Problem
import aguDataSystem.server.http.controllers.models.input.tank.TankCreationInputModel
import aguDataSystem.server.http.controllers.models.input.tank.TankUpdateInputModel
import aguDataSystem.server.http.controllers.models.output.agu.AGUOutputModel
import aguDataSystem.server.http.controllers.models.output.tank.AddTankOutputModel
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.errors.tank.AddTankError
import aguDataSystem.server.service.errors.tank.DeleteTankError
import aguDataSystem.server.service.errors.tank.UpdateTankError
import aguDataSystem.utils.Failure
import aguDataSystem.utils.Success
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for the tank operations
 */
@RestController("AGUs Tanks")
@RequestMapping(URIs.Tank.ROOT)
class TankController(private val service: AGUService) {

	/**
	 * Adds a tank to an AGU
	 *
	 * @param aguCui the CUI of the AGU to add the tank to
	 * @param tankInput the tank to add
	 */
	@PostMapping
	fun addTank(
		@PathVariable aguCui: String,
		@RequestBody tankInput: TankCreationInputModel
	): ResponseEntity<*> {
		return when (val res = service.addTank(aguCui, tankInput.toTank())) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity
				.created(URIs.Tank.tankByID(aguCui, res.value))
				.body(AddTankOutputModel(res.value))
		}
	}

	/**
	 * Updates a tank in an AGU
	 *
	 * @param aguCui the CUI of the AGU to change the tank in
	 * @param tankNumber the number of the tank to change
	 * @param tankInput the new tank info to change to
	 * @return the AGU with the changed tank
	 */
	@PutMapping(URIs.Tank.BY_ID)
	fun updateTank(
		@PathVariable aguCui: String,
		@PathVariable tankNumber: Int,
		@RequestBody tankInput: TankUpdateInputModel
	): ResponseEntity<*> {
		return when (val res = service.updateTank(aguCui, tankNumber, tankInput.toTankUpdateDTO())) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity.ok(AGUOutputModel(res.value))
		}
	}

	/**
	 * Deletes a tank from an AGU
	 *
	 * @param aguCui the CUI of the AGU to delete the tank from
	 * @param tankNumber the number of the tank to delete
	 * @return the AGU with the deleted tank
	 */
	@DeleteMapping(URIs.Tank.BY_ID)
	fun deleteTank(
		@PathVariable aguCui: String,
		@PathVariable tankNumber: Int
	): ResponseEntity<*> {
		return when (val res = service.deleteTank(aguCui, tankNumber)) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity.ok(res.value)
		}
	}

	/**
	 * Resolves the problem of adding a tank to an AGU
	 *
	 * @receiver the error to resolve
	 * @return the response entity to return
	 */
	private fun AddTankError.resolveProblem(): ResponseEntity<*> =
		when (this) {
			AddTankError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
			AddTankError.InvalidLevels -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidLevels)
			AddTankError.TankAlreadyExists -> Problem.response(
				HttpStatus.BAD_REQUEST.value(),
				Problem.TankAlreadyExists
			)

			AddTankError.InvalidCapacity -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCapacity)
			AddTankError.InvalidTankNumber -> Problem.response(
				HttpStatus.BAD_REQUEST.value(),
				Problem.InvalidTankNumber
			)
		}

	/**
	 * Resolves the problem of updating a tank in an AGU
	 *
	 * @receiver the error to resolve
	 * @return the response entity to return
	 */
	private fun UpdateTankError.resolveProblem(): ResponseEntity<*> =
		when (this) {
			UpdateTankError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
			UpdateTankError.InvalidLevels -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidLevels)
			UpdateTankError.TankNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.TankNotFound)
			UpdateTankError.InvalidCUI -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCUI)
			UpdateTankError.InvalidCapacity -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCapacity)
			UpdateTankError.InvalidTankNumber -> Problem.response(
				HttpStatus.BAD_REQUEST.value(),
				Problem.InvalidTankNumber
			)
		}

	/**
	 * Resolves the problem for deleting a Tank
	 *
	 * @receiver the error to resolve
	 * @return the response entity to return
	 */
	private fun DeleteTankError.resolveProblem(): ResponseEntity<*> =
		when (this) {
			DeleteTankError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
			DeleteTankError.InvalidCUI -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidCUI)
		}
}