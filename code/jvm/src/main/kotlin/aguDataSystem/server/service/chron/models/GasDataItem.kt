package aguDataSystem.server.service.chron.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model for the gas data item from a provider response.
 *
 * @property id The id of the data item
 * @property name The name of the data item
 * @property value The value of the data item
 * @property dateValue The date value of the data item
 * @property componentName The component name of the data item
 */
@Serializable
data class GasDataItem(
	val id: Int,
	val name: String,
	val value: Int,
	@SerialName("date_value") val dateValue: String,
	@SerialName("component_name") val componentName: String
) {
	companion object {
		const val TANK_LEVEL = "Nivel Depósito"
	}
}