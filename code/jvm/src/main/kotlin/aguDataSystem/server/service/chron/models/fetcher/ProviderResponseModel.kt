package aguDataSystem.server.service.chron.models.fetcher

import kotlinx.serialization.Serializable

/**
 * Model for the provider response.
 *
 * @property lastFetch Last Fetch Timestamp from the provider.
 * @property dataList The list of data items
 */
@Serializable
data class ProviderResponseModel(
	val lastFetch: String,
	val dataList: List<DataListItem>
)