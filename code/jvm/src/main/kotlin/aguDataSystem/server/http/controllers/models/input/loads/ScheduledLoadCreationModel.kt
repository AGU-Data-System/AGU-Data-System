package aguDataSystem.server.http.controllers.models.input.loads

import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import aguDataSystem.server.domain.load.TimeOfDay
import java.time.LocalDate

/**
 * Input model for creating a scheduled load.
 *
 * @property aguCui The AGU CUI.
 * @property date The date.
 * @property timeOfDay The time of day.
 * @property amount The amount.
 * @property isManual Whether the load is manual.
 */
data class ScheduledLoadCreationModel(
	val aguCui: String,
	val date: String,
	val timeOfDay: String,
	val amount: String,
	val isManual: String
) {
	fun toScheduledLoadCreationDTO() = ScheduledLoadCreationDTO(
		aguCui = this.aguCui,
		date = LocalDate.parse(this.date),
		timeOfDay = TimeOfDay.valueOf(this.timeOfDay.uppercase()),
		amount = this.amount.toDouble(),
		isManual = this.isManual.toBoolean()
	)
}