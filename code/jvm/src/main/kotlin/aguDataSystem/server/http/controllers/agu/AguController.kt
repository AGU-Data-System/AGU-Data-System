package aguDataSystem.server.http.controllers.agu

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.agu.models.addAgu.AGUCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.addAgu.toAGUCreationDTO
import aguDataSystem.server.service.agu.AGUService
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.utils.Failure
import aguDataSystem.utils.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("AGUs")
@RequestMapping(URIs.Agu.ROOT)
class AguController(private val service: AGUService) {

    @GetMapping(URIs.Agu.GET_BY_ID)
    fun getById(aguId: Int) {
        //TODO
    }


    @PostMapping(URIs.Agu.CREATE)
    fun create(@RequestBody aguInput: AGUCreationInputModel) : ResponseEntity<*>{
        val res = service.createAGU(aguInput.toAGUCreationDTO())
        return when(res) {
            is Failure -> res.value.resolveProblem()
            is Success -> ResponseEntity.created().body(res.value) //TODO: Fill location
        }
    }

    private fun AGUCreationError.resolveProblem() : ResponseEntity<*> =
//        when(this) {
            TODO("Insert possible errors outcomes")
            //AGUCreationError.DNONotFound -> Problem.response(404, Problem.dnoNotFound)
//        }
}