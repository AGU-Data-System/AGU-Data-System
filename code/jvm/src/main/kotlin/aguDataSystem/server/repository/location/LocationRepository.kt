package aguDataSystem.server.repository.location

import aguDataSystem.server.domain.Location

/**
 * Repository for locations.
 */
interface LocationRepository {

	/**
	 * Get all locations.
	 *
	 * @return List of all locations.
	 */
	fun getLocations(): List<Location>

	/**
	 * Add a location.
	 *
	 * @param location Location to add.
	 * @return Added location.
	 */
	fun addLocation(location: Location): Location
}