package aguDataSystem.server.domain

import java.time.ZonedDateTime

/**
 * Represents a Gas Load.
 *
 * @property reference The reference of the load. TODO check data type
 * @property timeOfTheDay The time of the day when the load was made. TODO check data type
 * @property amount The amount of the load.
 * @property distance The distance of the load.
 * @property loadTimestamp The timestamp when the load was made.
 * @property unloadTimestamp The timestamp when the load was unloaded if it was unloaded.
 */
data class Load(
	val reference: String,
	val timeOfTheDay: String,
	val amount: Double,
	val distance: Double,
	val loadTimestamp: ZonedDateTime,
	val unloadTimestamp: ZonedDateTime?
)