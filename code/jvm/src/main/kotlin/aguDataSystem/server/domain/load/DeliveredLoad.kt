package aguDataSystem.server.domain.load

import aguDataSystem.server.domain.company.TransportCompany
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Represents a Delivered Load.
 * A delivered load is a load that was delivered by a transport company at a certain time into an AGU.
 *
 * @property id The id of the load
 * @property aguCui The AGU cui
 * @property date The date of the load
 * @property timeOfDay The time of day of the load
 * @property company The transport company that delivered the load
 * @property unloadTimestamp The timestamp when the load was delivered
 */
data class DeliveredLoad(
    override val id: Int,
    override val aguCui: String,
    override val date: LocalDate,
    override val timeOfDay: TimeOfDay,
    val company: TransportCompany,
    val unloadTimestamp: LocalDateTime
): Load()
