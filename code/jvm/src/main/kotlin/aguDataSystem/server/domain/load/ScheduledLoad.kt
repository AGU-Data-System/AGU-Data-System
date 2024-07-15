package aguDataSystem.server.domain.load

import java.time.LocalDate

/**
 * Represents a Scheduled Load.
 * A scheduled load is a load scheduled manually or automatically.
 *
 * @property id The id of the load
 * @property aguCui The AGU cui
 * @property date The date of the load
 * @property timeOfDay The time of day of the load
 * @property amount The amount of gas in the load where 1.0 = 20 ton of gas
 * @property isManual Whether the load was scheduled manually or not
 * @property isConfirmed Whether the load was confirmed or not by the client
 */
data class ScheduledLoad(
	override val id: Int,
	override val aguCui: String,
	override val locationName: String,
	override val date: LocalDate,
	override val timeOfDay: TimeOfDay,
	override val amount: Double,
	val isManual: Boolean,
	val isConfirmed: Boolean,
) : Load()