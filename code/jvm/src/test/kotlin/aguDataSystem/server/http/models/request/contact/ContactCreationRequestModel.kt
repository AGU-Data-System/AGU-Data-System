package aguDataSystem.server.http.models.request.contact

import kotlinx.serialization.Serializable

/**
 * Model for creating a contact
 *
 * @param name Name of the contact
 * @param phone Phone number of the contact
 * @param type Type of the contact
 */
@Serializable
data class ContactCreationRequestModel(
	val name: String,
	val phone: String,
	val type: String
)