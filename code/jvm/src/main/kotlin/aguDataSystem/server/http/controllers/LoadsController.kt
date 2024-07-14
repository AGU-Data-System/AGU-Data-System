package aguDataSystem.server.http.controllers

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.models.input.loads.GetLoadsInputModel
import aguDataSystem.server.http.controllers.models.input.loads.ScheduledLoadCreationModel
import aguDataSystem.server.http.controllers.models.output.loads.BooleanLoadOutputModel
import aguDataSystem.server.http.controllers.models.output.loads.GetLoadOutputModel
import aguDataSystem.server.http.controllers.models.output.loads.GetLoadsForWeekListOutputModel
import aguDataSystem.server.http.controllers.models.output.loads.ScheduledLoadOutputModel
import aguDataSystem.server.service.loads.LoadsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController("Loads")
@RequestMapping(URIs.Loads.ROOT)
class LoadsController(private val service: LoadsService) {

    @GetMapping
    fun getLoadForDay(@RequestBody getLoadsInputModel: GetLoadsInputModel): ResponseEntity<*> {
        val load = service.getLoadForDay(getLoadsInputModel.cui, LocalDate.parse(getLoadsInputModel.day))
        return if (load == null) {
            ResponseEntity.ok("No load found for the day")
        } else {
            ResponseEntity.ok(GetLoadOutputModel.fromScheduledLoad(load))
        }
    }

    @PostMapping
    fun scheduleLoad(@RequestBody scheduledLoad: ScheduledLoadCreationModel): ResponseEntity<*> {
        val scheduledLoadId = service.scheduleLoad(scheduledLoad.toScheduledLoadCreationDTO())
        return ResponseEntity.ok(ScheduledLoadOutputModel(scheduledLoadId))
    }

    @DeleteMapping(URIs.Loads.BY_ID)
    fun removeLoad(@PathVariable loadId: Int): ResponseEntity<*> {
        return ResponseEntity.ok(BooleanLoadOutputModel(service.removeLoad(loadId)))
    }

    @PutMapping(URIs.Loads.BY_ID)
    fun changeLoadDay(@PathVariable loadId: Int, @RequestBody newDay: String): ResponseEntity<*> {
        return ResponseEntity.ok(BooleanLoadOutputModel(service.changeLoadDay(loadId, LocalDate.parse(newDay))))
    }

    @PutMapping(URIs.Loads.BY_ID + URIs.Loads.CONFIRM)
    fun confirmLoad(@PathVariable loadId: Int): ResponseEntity<*> {
        return ResponseEntity.ok(BooleanLoadOutputModel(service.confirmLoad(loadId)))
    }

    @GetMapping(URIs.Loads.WEEK)
    fun getLoadsForWeek(@RequestParam startDay: String, @RequestParam endDay: String ): ResponseEntity<*> {
        val loads = service.getLoadsForWeek(LocalDate.parse(startDay), LocalDate.parse(endDay))
        return ResponseEntity.ok(GetLoadsForWeekListOutputModel(startDay, endDay, loads))
    }
}