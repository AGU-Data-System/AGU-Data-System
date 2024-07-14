package aguDataSystem.server.http.controllers.models.output.loads

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.TimeOfDay
import java.time.LocalDate

/**
 * Represents the output model for a load.
 *
 * @property id The id of the load
 * @property aguCui The AGU cui
 * @property date The date of the load
 * @property timeOfDay The time of day of the load
 * @property amount The amount of gas in the load where 1.0 = 20 ton of gas
 * @property isManual Whether the load was scheduled manually or not
 * @property isConfirmed Whether the load was confirmed or not by the client
 */
data class GetLoadOutputModel(
    val id: Int,
    val aguCui: String,
    val date: LocalDate,
    val timeOfDay: TimeOfDay,
    val amount: Double,
    val isManual: Boolean,
    val isConfirmed: Boolean,
) {
    companion object {
        fun fromScheduledLoad(scheduledLoad: ScheduledLoad): GetLoadOutputModel {
            return GetLoadOutputModel(
                id = scheduledLoad.id,
                aguCui = scheduledLoad.aguCui,
                date = scheduledLoad.date,
                timeOfDay = scheduledLoad.timeOfDay,
                amount = scheduledLoad.amount,
                isManual = scheduledLoad.isManual,
                isConfirmed = scheduledLoad.isConfirmed
            )
        }


    }
}