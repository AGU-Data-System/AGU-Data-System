package aguDataSystem.server.repository.load

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import java.time.LocalDate
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

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
			.mapTo<ScheduledLoad>()
			.findFirst()
			.orElse(null)
	}

	/**
	 * Schedules a load.
	 *
	 * @param scheduledLoad The load to schedule
	 * @return The id of the scheduled load
	 */
	override fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO): Int {
		return handle.createUpdate(
			"""
			INSERT INTO scheduled_load (agu_cui, local_date, time_of_day, amount, is_manual)
			VALUES (:cui, :day, :timeOfDay, :amount, :isManual)
			"""
		)
			.bind("cui", scheduledLoad.aguCui)
			.bind("day", scheduledLoad.date)
			.bind("timeOfDay", scheduledLoad.timeOfDay.toString().lowercase())
			.bind("amount", scheduledLoad.amount)
			.bind("isManual", scheduledLoad.isManual)
			.executeAndReturnGeneratedKeys()
			.mapTo<Int>()
			.one()
	}

	/**
	 * Removes a load.
	 *
	 * @param loadId The id of the load to remove
	 * @return Whether the load was removed or not
	 */
	override fun removeLoad(loadId: Int): Boolean {
		return handle.createUpdate(
			"""
			DELETE FROM scheduled_load
			WHERE id = :id
			"""
		)
			.bind("id", loadId)
			.execute() > 0
	}

	/**
	 * Changes the day of a load.
	 *
	 * @param loadId The id of the load to change the day of
	 * @param newDay The new day of the load
	 * @return Whether the day was changed or not
	 */
	override fun changeLoadDay(loadId: Int, newDay: LocalDate): Boolean {
		return handle.createUpdate(
			"""
			UPDATE scheduled_load
			SET local_date = :newDay
			WHERE id = :id
			"""
		)
			.bind("newDay", newDay)
			.bind("id", loadId)
			.execute() > 0
	}

	/**
	 * Confirms a load.
	 *
	 * @param loadId The id of the load to confirm
	 * @return Whether the load was confirmed or not
	 */
	override fun confirmLoad(loadId: Int): Boolean {
		return handle.createUpdate(
			"""
			UPDATE scheduled_load
			SET is_confirmed = true
			WHERE id = :id
			"""
		)
			.bind("id", loadId)
			.execute() > 0
	}

	/**
	 * Gets all the loads for the week.
	 *
	 * @param startDay The start day of the week
	 * @param endDay The end day of the week
	 * @return The loads for the week
	 */
	override fun getLoadsForWeek(startDay: LocalDate, endDay: LocalDate): List<ScheduledLoad> {
		return handle.createQuery(
			"""
			SELECT * FROM scheduled_load
			WHERE local_date >= :startDay
			AND local_date <= :endDay
			"""
		)
			.bind("startDay", startDay)
			.bind("endDay", endDay)
			.mapTo<ScheduledLoad>()
			.list()
	}
}