package aguDataSystem.server.repository.load

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import java.time.LocalDate

/**
 * Represents a repository for loading data.
 */
interface LoadRepository {

	/**
	 * Gets the load for a specific day.
	 *
	 * @param cui The AGU cui
	 * @param day The day to get the load for
	 * @return The load for the day
	 */
	fun getLoadForDay(cui: String, day: LocalDate): ScheduledLoad?

	/**
	 * Schedules a load.
	 *
	 * @param scheduledLoad The load to schedule
	 * @return The id of the scheduled load
	 */
	fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO): Int

	/**
	 * Removes a load.
	 *
	 * @param loadId The id of the load to remove
	 * @return Whether the load was removed or not
	 */
	fun removeLoad(loadId: Int): Boolean

	/**
	 * Changes the day of a load.
	 *
	 * @param loadId The id of the load to change the day of
	 * @param newDay The new day of the load
	 * @return Whether the day was changed or not
	 */
	fun changeLoadDay(loadId: Int, newDay: LocalDate): Boolean

	/**
	 * Confirms a load.
	 *
	 * @param loadId The id of the load to confirm
	 * @return Whether the load was confirmed or not
	 */
	fun confirmLoad(loadId: Int): Boolean

	/**
	 * Gets the loads for the week.
	 *
	 * @param startDay The start day of the week
	 * @param endDay The end day of the week
	 * @return The loads for the week
	 */
	fun getLoadsForWeek(startDay: LocalDate, endDay: LocalDate): List<ScheduledLoad>
}