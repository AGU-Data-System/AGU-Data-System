package aguDataSystem.server.http.models.response.contact

import kotlinx.serialization.Serializable

/**
 * Response for the creation of a contact
 *
 * @property id the id of the contact
 */
@Serializable
data class ContactCreationResponse(
	val id: Int
)