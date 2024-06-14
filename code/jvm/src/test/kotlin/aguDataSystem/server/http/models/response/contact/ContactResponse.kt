package aguDataSystem.server.http.models.response.contact

import kotlinx.serialization.Serializable

/**
 * Response for a Contact
 *
 * @property id the id of the contact
 * @property name the name of the contact
 * @property phone the phone number of the contact
 * @property type the type of the contact
 */
@Serializable
data class ContactResponse(
	val id: Int,
	val name: String,
	val phone: String,
	val type: String
)