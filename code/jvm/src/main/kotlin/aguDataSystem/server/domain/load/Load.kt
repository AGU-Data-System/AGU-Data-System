package aguDataSystem.server.domain.load

import java.time.LocalDate

/**
 * Represents a Gas Load.
 *
 * @property id The id of the load
 * @property aguCui The AGU cui
 * @property date The date of the load
 * @property timeOfDay The time of day of the load
 * @property amount The amount of gas in the load where 1.0 = 20 ton of gas
 */
sealed class Load {
    abstract val id: Int
    abstract val aguCui: String
    abstract val date: LocalDate
    abstract val timeOfDay: TimeOfDay
    abstract val amount: Double
}