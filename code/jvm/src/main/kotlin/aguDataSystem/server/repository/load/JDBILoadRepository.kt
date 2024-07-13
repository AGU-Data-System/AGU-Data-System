package aguDataSystem.server.repository.load

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import java.time.LocalDate
import org.jdbi.v3.core.Handle

/**
 * JDBI implementation of [LoadRepository].
 */
class JDBILoadRepository(private val handle: Handle) : LoadRepository {

	/**
	 * Gets the load for a specific day.
	 *
	 * @param cui The AGU cui
	 * @param day The day to get the load for
	 * @return The load for the day
	 */
	override fun getLoadForDay(cui: String, day: LocalDate): ScheduledLoad? {
		return handle.createQuery(
			"""
			SELECT * FROM scheduled_load
			WHERE agu_cui = :cui
			AND local_date = :day
			"""
		)
			.bind("cui", cui)
			.bind("day", day)
			.mapTo(ScheduledLoad::class.java)
			.findFirst()
			.orElse(null)
	}

	/**
	 * Schedules a load.
	 *
	 * @param scheduledLoad The load to schedule
	 */
	override fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO) {
		handle.createUpdate(
			"""
			INSERT INTO scheduled_load (agu_cui, local_date, time_of_day, amount, is_manual)
			VALUES (:cui, :day, :timeOfDay, :amount, :isManual)
			"""
		)
			.bind("cui", scheduledLoad.aguCui)
			.bind("local_date", scheduledLoad.date)
			.bind("timeOfDay", scheduledLoad.timeOfDay)
			.bind("amount", scheduledLoad.amount)
			.bind("isManual", scheduledLoad.isManual)
			.execute()
	}

	/**
	 * Removes a load.
	 *
	 * @param loadId The id of the load to remove
	 */
	override fun removeLoad(loadId: Int) {
		handle.createUpdate(
			"""
			DELETE FROM scheduled_load
			WHERE id = :id
			"""
		)
			.bind("id", loadId)
			.execute()
	}
}