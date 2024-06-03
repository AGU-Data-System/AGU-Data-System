package aguDataSystem.server.http.controllers.agu.models.output.getAll

import aguDataSystem.server.domain.Location

/**
 * Output model for Location
 *
 * @param name The name of the location
 * @param latitude The latitude of the location
 * @param longitude The longitude of the location
 */
data class LocationOutputModel(
	val name: String,
	val latitude: Double,
	val longitude: Double
) {
	constructor(location: Location) : this(
		name = location.name,
		latitude = location.latitude,
		longitude = location.longitude
	)
}
