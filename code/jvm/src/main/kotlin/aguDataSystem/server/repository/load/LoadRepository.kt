package aguDataSystem.server.repository.load

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import java.time.LocalDate

interface LoadRepository {

	fun getLoadForDay(cui: String, day: LocalDate): ScheduledLoad?

	fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO)

	fun removeLoad(loadId: Int)
}