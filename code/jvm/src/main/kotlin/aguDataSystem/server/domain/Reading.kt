package aguDataSystem.server.domain

import java.time.LocalDateTime

/**
 * Represents a reading of gas level as a percentage in a given timestamp.
 *
 * @property timestamp the timestamp of the reading
 * @property gasLevel the gas level as a percentage
 */
class Reading(
	val timestamp: LocalDateTime,
	val gasLevel: Double,
)