package aguDataSystem.server.http.models.transportCompany

import kotlinx.serialization.Serializable

@Serializable
data class TransportCompanyRequestModel(
	val name: String
)