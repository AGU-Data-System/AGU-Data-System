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
	 */
	fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO)

	/**
	 * Removes a load.
	 *
	 * @param loadId The id of the load to remove
	 */
	fun removeLoad(loadId: Int)
}