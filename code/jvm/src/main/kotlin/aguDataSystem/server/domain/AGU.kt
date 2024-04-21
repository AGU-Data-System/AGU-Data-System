package aguDataSystem.server.domain

import java.awt.Image

/**
 * Represents the Autonomous Gas Unit (AGU) in the system.
 *
 * @property id unique identifier of the AGU
 * @property name name of the AGU
 * @property cui CUI of the AGU
 * @property isFavorite whether the AGU is a favorite
 * @property minLevel minimum level of the AGU
 * @property maxLevel maximum level of the AGU
 * @property criticalLevel critical level of the AGU
 * @property loadVolume load volume of the AGU
 * @property capacity capacity of the AGU
 * @property latitude latitude of the AGU
 * @property longitude longitude of the AGU
 * @property locationName name of the location of the AGU
 * @property dnoId DNO id of the AGU
 * @property notes notes of the AGU
 * @property training training of the AGU
 * @property image image of the AGU
 * @property contacts contacts of the AGU
 * @property readings readings of the AGU
 */
data class AGU(
	val id: Int,
	val name: String,
	val cui: String,
	val isFavorite: Boolean = false,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val loadVolume: Int,
	val capacity: Int,
	val latitude: Double,
	val longitude: Double,
	val locationName: String,
	val dnoId: Int,
	val notes: String,
	val training: String,
	val image: Image,
	val contacts: List<Contact>,
	val readings: List<Reading>,
)