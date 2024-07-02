package aguDataSystem.server.domain.load

import java.time.LocalDate

/**
 * Represents a DTO model for creating a new [ScheduledLoad]
 */
data class ScheduledLoadCreationDTO(
    val aguCui: String,
    val date: LocalDate,
    val timeOfDay: TimeOfDay = TimeOfDay.MORNING,
    val amount: Double = 1.0,
    val isManual: Boolean = false
)