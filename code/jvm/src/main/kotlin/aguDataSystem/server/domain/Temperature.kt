package aguDataSystem.server.domain

import java.time.LocalDateTime

/**
 * Represents a temperature for a specific date in the future or if
 * [date] is the same as [fetchTimeStamp] it represents the current temperature.
 *
 * @property date the date of the temperature prediction
 * @property min the minimum temperature for the given date
 * @property max the maximum temperature for the given date
 * @property fetchTimeStamp the timestamp when the temperature was fetched
 * @property location the location of the temperature
 */
class Temperature(
	val date: LocalDateTime,
	val min: Double,
	val max: Double,
	val fetchTimeStamp: LocalDateTime,
	val location: Location
) {

	/**
	 * The number of days ahead the temperature prediction is.
	 */
	val nrOfDaysAhead: Int
		get() = date.dayOfYear - fetchTimeStamp.dayOfYear

}