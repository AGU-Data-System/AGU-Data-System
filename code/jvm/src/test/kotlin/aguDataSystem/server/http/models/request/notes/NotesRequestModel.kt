package aguDataSystem.server.http.models.request.notes

import kotlinx.serialization.Serializable

/**
 * Request model for notes
 *
 * @param notes the AGU Notes
 */
@Serializable
data class NotesRequestModel(
	val notes: String?
)