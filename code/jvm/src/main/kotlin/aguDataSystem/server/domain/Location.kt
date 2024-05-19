package aguDataSystem.server.domain

/**
 * Represents a location
 *
 * @property name name of the location
 * @property latitude latitude of the location
 * @property longitude longitude of the location
 */
data class Location(
	val name: String,
	val latitude: Double,
	val longitude: Double
) {
	/**
	 * Returns a string representation of the object
	 */
	override fun toString(): String {
		return "Location(name = $name, latitude = ${latitude}º, longitude = ${longitude}º)"
	}
}