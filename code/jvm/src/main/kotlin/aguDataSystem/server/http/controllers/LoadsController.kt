package aguDataSystem.server.http.controllers

import aguDataSystem.server.http.URIs
import aguDataSystem.server.http.controllers.models.input.loads.GetLoadsInputModel
import aguDataSystem.server.http.controllers.models.input.loads.NewLoadDayInputModel
import aguDataSystem.server.http.controllers.models.input.loads.ScheduledLoadCreationModel
import aguDataSystem.server.http.controllers.models.output.loads.BooleanLoadOutputModel
import aguDataSystem.server.http.controllers.models.output.loads.GetLoadOutputModel
import aguDataSystem.server.http.controllers.models.output.loads.GetLoadsForWeekListOutputModel
import aguDataSystem.server.http.controllers.models.output.loads.ScheduledLoadOutputModel
import aguDataSystem.server.service.loads.LoadsService
import java.time.LocalDate
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

/**
 * Controller for the loads' endpoint.
 */
@RestController("Loads")
@RequestMapping(URIs.Loads.ROOT)
class LoadsController(private val service: LoadsService) {

	/**
	 * Gets the load for a day.
	 *
	 * @param getLoadsInputModel The input model.
	 * @return The load for the day.
	 */
	@GetMapping
	fun getLoadForDay(@RequestBody getLoadsInputModel: GetLoadsInputModel): ResponseEntity<*> {
		val load = service.getLoadForDay(getLoadsInputModel.cui, LocalDate.parse(getLoadsInputModel.day))
		return if (load == null) {
			ResponseEntity.ok("No load found for the day")
		} else {
			ResponseEntity.ok(GetLoadOutputModel.fromScheduledLoad(load))
		}
	}

	/**
	 * Schedules a load.
	 *
	 * @param scheduledLoad The scheduled load.
	 * @return The scheduled load id.
	 */
	@PostMapping
	fun scheduleLoad(@RequestBody scheduledLoad: ScheduledLoadCreationModel): ResponseEntity<*> {
		val scheduledLoadId = service.scheduleLoad(scheduledLoad.toScheduledLoadCreationDTO())
		return ResponseEntity.ok(ScheduledLoadOutputModel(scheduledLoadId))
	}

	/**
	 * Removes a load by its id.
	 *
	 * @param loadId The load id.
	 * @return The result of the operation.
	 */
	@DeleteMapping(URIs.Loads.BY_ID)
	fun removeLoad(@PathVariable loadId: String): ResponseEntity<*> {
		return ResponseEntity.ok(BooleanLoadOutputModel(service.removeLoad(loadId.toInt())))
	}

	/**
	 * Changes the day of a load.
	 *
	 * @param loadId The load id.
	 * @param newDay The new day.
	 * @return The result of the operation.
	 */
	@PutMapping(URIs.Loads.BY_ID)
	fun changeLoadDay(@PathVariable loadId: String, @RequestBody newDay: NewLoadDayInputModel): ResponseEntity<*> {
		return ResponseEntity.ok(
			BooleanLoadOutputModel(
				service.changeLoadDay(
					loadId.toInt(),
					LocalDate.parse(newDay.newDay)
				)
			)
		)
	}

	/**
	 * Confirms a load.
	 *
	 * @param loadId The load id.
	 * @return The result of the operation.
	 */
	@PutMapping(URIs.Loads.BY_ID + URIs.Loads.CONFIRM)
	fun confirmLoad(@PathVariable loadId: String): ResponseEntity<*> {
		return ResponseEntity.ok(BooleanLoadOutputModel(service.confirmLoad(loadId.toInt())))
	}

	/**
	 * Gets the loads for a week.
	 *
	 * @param startDay The start day.
	 * @param endDay The end day.
	 * @return The loads for the week.
	 */
	@GetMapping(URIs.Loads.WEEK)
	fun getLoadsForWeek(@RequestParam startDay: String, @RequestParam endDay: String): ResponseEntity<*> {
		val loads = service.getLoadsForWeek(LocalDate.parse(startDay), LocalDate.parse(endDay))
		return ResponseEntity.ok(GetLoadsForWeekListOutputModel(startDay, endDay, loads))
	}
}