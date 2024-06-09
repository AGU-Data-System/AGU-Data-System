package aguDataSystem.server.http.models.notes

import kotlinx.serialization.Serializable

@Serializable
data class NotesRequestModel(
	val notes: String?
)