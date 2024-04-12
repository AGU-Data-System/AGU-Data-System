package aguDataSystem.server.domain

import java.awt.Image

/**
 * Represents the Autonomous Gas Unit (AGU) in the system.
 *
 * @property id The unique identifier of the AGU.
 * @property cui The CUI of the AGU.
 * @property capacity The max capacity of the AGU in Tons.
 * @property image The image of the AGU.
 * @property isFavorite Indicates if the AGU is a favorite.
 * @property notes The notes of the AGU.
 * @property contacts The contacts of the AGU.
 * @property readings The readings of the AGU.
 * @property minLevel The minimum accepted gas level of the AGU.
 * @property maxLevel The maximum accepted gas level of the AGU.
 * @property criticalLevel The critical gas level of the AGU.
 * @property location The location of the AGU.
 */
class AGU(
	val id: Int,
	val cui: String,
	val capacity: Int,
	val image: Image,
	val isFavorite: Boolean,
	val notes: List<String>,
	val contacts: List<Contact>,
	val readings: List<Reading>,
	val minLevel: Int,
	val maxLevel: Int,
	val criticalLevel: Int,
	val location: Location,
)