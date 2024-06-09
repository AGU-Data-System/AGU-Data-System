package aguDataSystem.server.http.models.dno

import kotlinx.serialization.Serializable

@Serializable
data class DNOCreationRequestModel(
	val name: String,
	val region: String
)