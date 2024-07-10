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
		TODO("Not yet implemented")
	}

	/**
	 * Schedules a load.
	 *
	 * @param scheduledLoad The load to schedule
	 */
	override fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO) {
		TODO("Not yet implemented")
	}

	/**
	 * Removes a load.
	 *
	 * @param loadId The id of the load to remove
	 */
	override fun removeLoad(loadId: Int) {
		TODO("Not yet implemented")
	}
}