package aguDataSystem.server.http.controllers

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.media.Problem
import aguDataSystem.server.http.controllers.models.input.dno.DNOCreationInputModel
import aguDataSystem.server.http.controllers.models.output.dno.DNOListOutputModel
import aguDataSystem.server.http.controllers.models.output.dno.DNOOutputModel
import aguDataSystem.server.service.dno.DNOService
import aguDataSystem.server.service.errors.dno.CreateDNOError
import aguDataSystem.utils.Failure
import aguDataSystem.utils.Success
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("DNOs")
@RequestMapping(URIs.Dno.ROOT)
class DnoController(private val service: DNOService) {

    /**
     * Gets the DNOs
     *
     * @return the list of DNOs
     */
    @GetMapping
    fun getDnos(): ResponseEntity<*> {
        return ResponseEntity.ok(DNOListOutputModel(service.getDnos()))
    }

    /**
     * Adds a DNO
     *
     * @param dnoCreationInputModel the DNO to add
     * @return the created DNO
     */
    @PostMapping
    fun addDno(
        @RequestBody dnoCreationInputModel: DNOCreationInputModel
    ): ResponseEntity<*> {
        return when (val res = service.createDNO(dnoCreationInputModel.toDNOCreationDTO())) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.created(URIs.Dno.byID(res.value.id)).body(DNOOutputModel(res.value))
        }
    }

    /**
     * Deletes a DNO
     *
     * @param dnoId the CUI of the DNO to delete
     * @return the deleted DNO
     */
    @DeleteMapping(URIs.Dno.BY_ID)
    fun deleteDno(
        @PathVariable dnoId: Int
    ): ResponseEntity<*> {
        return ResponseEntity.ok(service.deleteDNO(dnoId))
    }

    /**
     * Resolves the problem of adding a DNO
     *
     * @receiver the error to resolve
     * @return the response entity to return
     */
    private fun CreateDNOError.resolveProblem(): ResponseEntity<*> {
        return when (this) {
            CreateDNOError.DNOAlreadyExists -> Problem.response(HttpStatus.CONFLICT.value(), Problem.DnoAlreadyExists)
            CreateDNOError.InvalidName -> Problem.response(HttpStatus.BAD_REQUEST.value(), Problem.InvalidName)
        }
    }

}