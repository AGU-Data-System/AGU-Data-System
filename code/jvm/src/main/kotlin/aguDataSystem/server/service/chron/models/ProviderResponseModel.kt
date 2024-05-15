package aguDataSystem.server.service.chron.models

import kotlinx.serialization.Serializable

/**
 * Model for the provider response.
 *
 * @property dataList The list of data items
 */
@Serializable
data class ProviderResponseModel(
	val dataList: List<DataListItem>
)