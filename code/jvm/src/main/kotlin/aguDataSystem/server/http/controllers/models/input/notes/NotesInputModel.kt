package aguDataSystem.server.http.controllers.models.input.notes

import aguDataSystem.server.domain.agu.AGU

/**
 * Represents the input model for updating notes of an [AGU]
 *
 * @property notes The notes of the [AGU]
 */
data class NotesInputModel(
	val notes: String
)