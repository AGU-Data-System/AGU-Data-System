package aguDataSystem.server.service.chron.models.fetcher

import kotlinx.serialization.Serializable

/**
 * Model for the data list item from a provider response.
 *
 * @property fetchTime The time the data was fetched
 * @property data The data
 */
@Serializable
data class DataListItem(
	val fetchTime: String,
	val data: String
)