package aguDataSystem.server.repository.load

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import java.time.LocalDate
import org.jdbi.v3.core.Handle

class JDBILoadRepository(private val handle: Handle): LoadRepository{
	override fun getLoadForDay(cui: String, day: LocalDate): ScheduledLoad? {
		TODO("Not yet implemented")
	}

	override fun scheduleLoad(scheduledLoad: ScheduledLoadCreationDTO) {
		TODO("Not yet implemented")
	}
}