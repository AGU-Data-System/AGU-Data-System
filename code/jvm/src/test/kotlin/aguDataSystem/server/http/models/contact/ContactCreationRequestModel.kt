package aguDataSystem.server.http.models.contact

import kotlinx.serialization.Serializable

@Serializable
data class ContactCreationRequestModel(
	val name: String,
	val phone: String,
	val type: String
)