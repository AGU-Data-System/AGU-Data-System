package aguDataSystem.server.domain.load

import java.time.LocalDate

/**
 * Represents a DTO model for creating a new [ScheduledLoad]
 *
 * @param aguCui The AGU cui
 * @param date The date of the load
 * @param timeOfDay The time of day of the load
 * @param amount The amount of gas in the load where 1.0 = 20 ton of gas
 * @param isManual Whether the load was scheduled manually or not
 */
data class ScheduledLoadCreationDTO(
	val aguCui: String,
	val date: LocalDate,
	val timeOfDay: TimeOfDay = TimeOfDay.MORNING,
	val amount: Double = 1.0,
	val isManual: Boolean = false
)