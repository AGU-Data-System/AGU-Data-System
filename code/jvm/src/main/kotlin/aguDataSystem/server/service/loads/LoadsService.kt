package aguDataSystem.server.service.loads

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import aguDataSystem.server.repository.TransactionManager
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Represents a service for loading data.
 *
 * @property transactionManager The transaction manager
 */
@Service
class LoadsService(
    private val transactionManager: TransactionManager
) {
    fun getLoadForDay(cui: String, day: LocalDate): ScheduledLoad? {
        return transactionManager.run {
            it.loadRepository.getLoadForDay(cui, day)
        }
    }

    fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO): Int {
        return transactionManager.run {
            it.loadRepository.scheduleLoad(scheduledLoad)
        }
    }

    fun removeLoad(loadId: Int): Boolean {
        return transactionManager.run {
            it.loadRepository.removeLoad(loadId)
        }
    }

    fun changeLoadDay(loadId: Int, newDay: LocalDate): Boolean {
        return transactionManager.run {
            it.loadRepository.changeLoadDay(loadId, newDay)
        }
    }

    fun confirmLoad(loadId: Int): Boolean {
        return transactionManager.run {
            it.loadRepository.confirmLoad(loadId)
        }
    }

    fun getLoadsForWeek(startDay: LocalDate, endDay: LocalDate): List<ScheduledLoad> {
        return transactionManager.run {
            it.loadRepository.getLoadsForWeek(startDay, endDay)
        }
    }
}