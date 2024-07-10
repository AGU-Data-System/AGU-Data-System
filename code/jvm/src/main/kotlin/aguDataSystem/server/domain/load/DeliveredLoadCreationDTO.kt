package aguDataSystem.server.domain.load

/**
 * Represents a DTO model for creating a new [DeliveredLoad]
 *
 * @param aguCui The AGU cui
 * @param date The date of the load
 * @param timeOfDay The time of day of the load
 * @param amount The amount of gas in the load where 1.0 = 20 ton of gas
 * @param company The transport company that delivered the load
 * @param unloadTimestamp The timestamp when the load was delivered
 */
data class DeliveredLoadCreationDTO(
	val aguCui: String,
	val date: String,
	val timeOfDay: String,
	val amount: Double,
	val company: Int,
	val unloadTimestamp: String
)
