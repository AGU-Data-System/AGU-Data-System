package aguDataSystem.server.http.controllers.models.input.loads

import aguDataSystem.server.domain.load.ScheduledLoadCreationDTO
import aguDataSystem.server.domain.load.TimeOfDay
import java.time.LocalDate

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