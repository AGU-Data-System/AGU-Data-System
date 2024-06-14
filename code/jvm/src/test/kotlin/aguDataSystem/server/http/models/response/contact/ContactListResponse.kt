package aguDataSystem.server.http.models.response.contact

import kotlinx.serialization.Serializable

/**
 * Response for a list of contacts
 *
 * @property contacts the list of contacts
 * @property size the size of the list
 */
@Serializable
class ContactListResponse(
	val contacts: List<ContactResponse>,
	val size: Int
)