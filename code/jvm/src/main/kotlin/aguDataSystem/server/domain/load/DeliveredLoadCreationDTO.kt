package aguDataSystem.server.domain.load

/**
 * Represents a DTO model for creating a new [DeliveredLoad]
 */
data class DeliveredLoadCreationDTO(
    val aguCui: String,
    val date: String,
    val timeOfDay: String,
    val amount: Double,
    val company: Int,
    val unloadTimestamp: String
)
