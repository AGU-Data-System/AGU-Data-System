package aguDataSystem.server.service.loads

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import aguDataSystem.server.repository.TransactionManager
import java.time.LocalDate
import org.springframework.stereotype.Service

/**
 * Represents a service for loading data.
 *
 * @property transactionManager The transaction manager
 */
@Service
class LoadsService(
	private val transactionManager: TransactionManager
) {
	/**
	 * Gets the load for a day.
	 *
	 * @param cui The CUI
	 * @param day The day
	 *
	 * @return The load for the day
	 */
	fun getLoadForDay(cui: String, day: LocalDate): ScheduledLoad? {
		return transactionManager.run {
			it.loadRepository.getLoadForDay(cui, day)
		}
	}

	/**
	 * Schedules a load.
	 *
	 * @param scheduledLoad The load to be scheduled
	 *
	 * @return The id of the scheduled load
	 */
	fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO): Int {
		return transactionManager.run {
			it.loadRepository.scheduleLoad(scheduledLoad)
		}
	}

	/**
	 * Removes a load.
	 *
	 * @param loadId The id of the load to be removed
	 *
	 * @return True if the load was removed, false otherwise
	 */
	fun removeLoad(loadId: Int): Boolean {
		return transactionManager.run {
			it.loadRepository.removeLoad(loadId)
		}
	}

	/**
	 * Changes the day of a load.
	 *
	 * @param loadId The id of the load to be changed
	 * @param newDay The new day
	 *
	 * @return True if the day was changed, false otherwise
	 */
	fun changeLoadDay(loadId: Int, newDay: LocalDate): Boolean {
		return transactionManager.run {
			it.loadRepository.changeLoadDay(loadId, newDay)
		}
	}

	/**
	 * Confirms a load.
	 *
	 * @param loadId The id of the load to be confirmed
	 *
	 * @return True if the load was confirmed, false otherwise
	 */
	fun confirmLoad(loadId: Int): Boolean {
		return transactionManager.run {
			it.loadRepository.confirmLoad(loadId)
		}
	}

	/**
	 * Gets the loads for a week.
	 *
	 * @param startDay The start day
	 * @param endDay The end day
	 *
	 * @return The loads for the week
	 */
	fun getLoadsForWeek(startDay: LocalDate, endDay: LocalDate): List<ScheduledLoad> {
		return transactionManager.run {
			it.loadRepository.getLoadsForWeek(startDay, endDay)
		}
	}
}