package aguDataSystem.server.http

import java.net.URI
import org.springframework.web.util.UriTemplate

object URIs {

	const val PREFIX = "/api"
	const val HOME = "$PREFIX/"

	fun home() = URI(HOME)

	/**
	 * Contains the URIs for the agu endpoints
	 */
	object Agu {
		const val ROOT = "$PREFIX/agus"
		const val GET_BY_ID = "/{aguCui}"
		const val CREATE = "/create"
		const val GET_TEMPERATURE = "/{aguCui}/temperature"
		const val GET_DAILY_GAS_LEVELS = "/{aguCui}/gas/daily"
		const val GET_HOURLY_GAS_LEVELS = "/{aguCui}/gas/hourly"
		const val GET_PREDICTION_GAS_LEVELS = "/{aguCui}/gas/predictions"
		const val GET_FAVOURITE_AGUS = "/favourites"
		const val PUT_FAVOURITE_AGUS = "/{aguCui}/favourite"
		const val CONTACT = "/{aguCui}/contact"
		const val CONTACT_BY_ID = "/{aguCui}/contact/{contactId}"
		const val TANK = "/{aguCui}/tank"
		const val TANK_BY_ID = "/{aguCui}/tank/{tankNumber}"
		const val LEVELS = "/{aguCui}/levels"
		const val NOTES = "/{aguCui}/notes"

		fun byID(id: String) = UriTemplate(ROOT + GET_BY_ID).expand(id)
		fun home() = URI(HOME)
		fun register() = URI(CREATE)
		fun temperature(id: String) = UriTemplate(ROOT + GET_TEMPERATURE).expand(id)
		fun dailyGasLevels(id: String) = UriTemplate(ROOT + GET_DAILY_GAS_LEVELS).expand(id)
		fun hourlyGasLevels(id: String) = UriTemplate(ROOT + GET_HOURLY_GAS_LEVELS).expand(id)
		fun predictionGasLevels(id: String) = UriTemplate(ROOT + GET_PREDICTION_GAS_LEVELS).expand(id)
		fun favouriteAgus() = URI(ROOT + GET_FAVOURITE_AGUS)
		fun putFavouriteAgus(id: String) = UriTemplate(ROOT + PUT_FAVOURITE_AGUS).expand(id)
		fun contact(id: String) = UriTemplate(ROOT + CONTACT).expand(id)
		fun contactByID(cui: String, id: Int) = UriTemplate(ROOT + CONTACT_BY_ID).expand(cui, id)
		fun tank(id: String) = UriTemplate(ROOT + TANK).expand(id)
		fun tankByID(cui: String, id: Int) = UriTemplate(ROOT + TANK_BY_ID).expand(cui, id)
		fun levels(id: String) = UriTemplate(ROOT + LEVELS).expand(id)
		fun notes(id: String) = UriTemplate(ROOT + NOTES).expand(id)
	}

}