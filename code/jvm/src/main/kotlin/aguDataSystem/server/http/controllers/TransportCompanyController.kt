package aguDataSystem.server.http.controllers

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.media.Problem
import aguDataSystem.server.http.controllers.models.input.transportCompany.TransportCompanyCreationInputModel
import aguDataSystem.server.http.controllers.models.output.transportCompany.TransportCompanyListOutputModel
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyError
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyToAGUError
import aguDataSystem.server.service.errors.transportCompany.DeleteTransportCompanyFromAGUError
import aguDataSystem.server.service.errors.transportCompany.GetTransportCompaniesOfAGUError
import aguDataSystem.server.service.transportCompany.TransportCompanyService
import aguDataSystem.utils.Failure
import aguDataSystem.utils.Success
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for the Transport Company operations
 */
@RestController("TransportCompany")
@RequestMapping(URIs.TransportCompany.ROOT)
class TransportCompanyController(private val service: TransportCompanyService) {

	/**
	 * Gets the transport companies
	 *
	 * @return the list of transport companies
	 */
	@GetMapping
	fun getTransportCompanies(): ResponseEntity<*> {
		return ResponseEntity.ok(TransportCompanyListOutputModel(service.getTransportCompanies()))
	}

	/**
	 * Gets the transport companies of an AGU
	 *
	 * @param aguCui the CUI of the AGU to get the transport companies from
	 * @return the list of transport companies of the AGU
	 */
	@GetMapping(URIs.TransportCompany.ALL_BY_CUI)
	fun getTransportCompaniesOfAGU(
		@PathVariable aguCui: String
	): ResponseEntity<*> {
		return when (val res = service.getTransportCompaniesOfAGU(aguCui)) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity.ok(TransportCompanyListOutputModel(res.value))
		}
	}

	/**
	 * Adds a transport company
	 *
	 * @param transportCompanyCreationInputModel the transport company to add
	 * @return the created transport company
	 */
	@PostMapping
	fun addTransportCompany(
		@RequestBody transportCompanyCreationInputModel: TransportCompanyCreationInputModel
	): ResponseEntity<*> {
		return when (val res =
			service.addTransportCompany(transportCompanyCreationInputModel.toTransportCompanyCreationDTO())) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity.created(URIs.TransportCompany.byID(res.value)).body(res.value)
		}
	}

	/**
	 * Deletes a transport company
	 *
	 * @param transportCompanyId the ID of the transport company to delete
	 */
	@DeleteMapping(URIs.TransportCompany.BY_ID)
	fun deleteTransportCompany(
		@PathVariable transportCompanyId: Int
	): ResponseEntity<*> {
		return ResponseEntity.ok(service.deleteTransportCompany(transportCompanyId))
	}

	/**
	 * Puts a transport company as a transport company of an AGU
	 * @param aguCui the CUI of the AGU to add the transport company to
	 * @param transportCompanyId the ID of the transport company to add
	 */
	@PutMapping(URIs.TransportCompany.BY_ID_AND_CUI)
	fun addTransportCompanyToAGU(
		@PathVariable aguCui: String,
		@PathVariable transportCompanyId: Int
	): ResponseEntity<*> {
		return when (val res = service.addTransportCompanyToAGU(aguCui, transportCompanyId)) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity.ok(res.value)
		}
	}

	/**
	 * Deletes a transport company from an AGU
	 * @param aguCui the CUI of the AGU to delete the transport company from
	 * @param transportCompanyId the ID of the transport company to delete
	 */
	@DeleteMapping(URIs.TransportCompany.BY_ID_AND_CUI)
	fun deleteTransportCompanyFromAGU(
		@PathVariable aguCui: String,
		@PathVariable transportCompanyId: Int
	): ResponseEntity<*> {
		return when (val res = service.deleteTransportCompanyFromAGU(aguCui, transportCompanyId)) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity.ok(res.value)
		}
	}

	/**
	 * Resolve the problem of getting the transport companies of an AGU
	 *
	 * @receiver the error to resolve
	 * @return the response entity with the problem
	 */
	private fun GetTransportCompaniesOfAGUError.resolveProblem(): ResponseEntity<*> {
		return when (this) {
			GetTransportCompaniesOfAGUError.AGUNotFound -> Problem.response(
				HttpStatus.NOT_FOUND.value(),
				Problem.AGUNotFound
			)
		}
	}

	/**
	 * Resolve the problem of adding a transport company
	 *
	 * @receiver the error to resolve
	 * @return the response entity with the problem
	 */
	private fun AddTransportCompanyError.resolveProblem(): ResponseEntity<*> {
		return when (this) {
			AddTransportCompanyError.TransportCompanyAlreadyExists -> Problem.response(
				HttpStatus.CONFLICT.value(),
				Problem.TransportCompanyAlreadyExists
			)

			AddTransportCompanyError.InvalidName -> Problem.response(
				HttpStatus.BAD_REQUEST.value(),
				Problem.InvalidName
			)
		}
	}

	/**
	 * Resolve the problem of adding a transport company to an AGU
	 *
	 * @receiver the error to resolve
	 * @return the response entity with the problem
	 */
	private fun AddTransportCompanyToAGUError.resolveProblem(): ResponseEntity<*> {
		return when (this) {
			AddTransportCompanyToAGUError.AGUNotFound -> Problem.response(
				HttpStatus.NOT_FOUND.value(),
				Problem.AGUNotFound
			)

			AddTransportCompanyToAGUError.TransportCompanyNotFound -> Problem.response(
				HttpStatus.NOT_FOUND.value(),
				Problem.TransportCompanyNotFound
			)
		}
	}

	/**
	 * Resolve the problem of deleting a transport company from an AGU
	 *
	 * @receiver the error to resolve
	 * @return the response entity with the problem
	 */
	private fun DeleteTransportCompanyFromAGUError.resolveProblem(): ResponseEntity<*> {
		return when (this) {
			DeleteTransportCompanyFromAGUError.AGUNotFound -> Problem.response(
				HttpStatus.NOT_FOUND.value(),
				Problem.AGUNotFound
			)

			DeleteTransportCompanyFromAGUError.TransportCompanyNotFound -> Problem.response(
				HttpStatus.NOT_FOUND.value(),
				Problem.TransportCompanyNotFound
			)
		}
	}
}