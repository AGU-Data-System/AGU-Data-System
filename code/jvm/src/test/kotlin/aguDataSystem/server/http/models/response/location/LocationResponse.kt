package aguDataSystem.server.http.models.response.location

import kotlinx.serialization.Serializable

/**
 * Represents a location response
 *
 * @property name the name of the location
 * @property latitude the latitude of the location
 * @property longitude the longitude of the location
 */
@Serializable
data class LocationResponse(
	val name: String,
	val latitude: Double,
	val longitude: Double
)