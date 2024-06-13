package aguDataSystem.server.http.controllers

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.media.Problem
import aguDataSystem.server.http.controllers.models.input.contact.ContactCreationInputModel
import aguDataSystem.server.http.controllers.models.output.contact.AddContactOutputModel
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.errors.contact.AddContactError
import aguDataSystem.server.service.errors.contact.DeleteContactError
import aguDataSystem.utils.Failure
import aguDataSystem.utils.Success
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for the AGU contacts operations
 */
@RestController("AGUs Contacts")
@RequestMapping(URIs.Contact.ROOT)
class ContactController(private val service: AGUService) {

	/**
	 * Adds a contact to an AGU
	 *
	 * @param aguCui the CUI of the AGU to add the contact to
	 * @param contact the contact to add
	 * @return the added contact
	 */
	@PostMapping
	fun addContact(
		@PathVariable aguCui: String,
		@RequestBody contact: ContactCreationInputModel
	): ResponseEntity<*> {
		return when (val res = service.addContact(aguCui, contact.toContactCreationDTO())) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity
				.created(URIs.Contact.contactByID(aguCui, res.value))
				.body(AddContactOutputModel(res.value))
		}
	}

	/**
	 * Deletes a contact from an AGU
	 *
	 * @param aguCui the CUI of the AGU to delete the contact from
	 * @param contactId the ID of the contact to delete
	 * @return the deleted contact
	 */
	@DeleteMapping(URIs.Contact.BY_ID)
	fun deleteContact(
		@PathVariable aguCui: String,
		@PathVariable contactId: Int
	): ResponseEntity<*> {
		return when (val res = service.deleteContact(aguCui, contactId)) {
			is Failure -> res.value.resolveProblem()
			is Success -> ResponseEntity.ok(res.value)
		}
	}

	/**
	 * Resolve the problem of adding a contact to an AGU
	 *
	 * @receiver the error to resolve
	 * @return the response entity to return
	 */
	private fun AddContactError.resolveProblem(): ResponseEntity<*> =
		when (this) {
			AddContactError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
			AddContactError.InvalidContact -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidContact)
			AddContactError.InvalidContactType -> Problem.response(
				HttpStatus.BAD_REQUEST.value(),
				Problem.InvalidContactType
			)

			AddContactError.ContactAlreadyExists -> Problem.response(
				HttpStatus.BAD_REQUEST.value(),
				Problem.ContactAlreadyExists
			)
		}

	/**
	 * Resolves the problem of deleting a contact from an AGU
	 *
	 * @receiver the error to resolve
	 * @return the response entity to return
	 */
	private fun DeleteContactError.resolveProblem(): ResponseEntity<*> =
		when (this) {
			DeleteContactError.AGUNotFound -> Problem.response(HttpStatus.NOT_FOUND.value(), Problem.AGUNotFound)
		}
}