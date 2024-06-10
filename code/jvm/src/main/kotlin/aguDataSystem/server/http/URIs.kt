package aguDataSystem.server.http

import java.net.URI
import org.springframework.web.util.UriTemplate

/**
 * Contains the URIs for the API
 */
object URIs {

	const val PREFIX = "/api"
	private const val HOME = "$PREFIX/"

	fun home() = URI(HOME)

	/**
	 * Contains the URIs for the agu endpoints
	 */
	object Agu {
		const val ROOT = "$PREFIX/agus"
		const val BY_ID = "/{aguCui}"
		const val CREATE = "/create"
		const val TEMPERATURE = "/{aguCui}/temperature"
		const val DAILY_GAS_LEVELS = "/{aguCui}/gas/daily"
		const val HOURLY_GAS_LEVELS = "/{aguCui}/gas/hourly"
		const val PREDICTION_GAS_LEVELS = "/{aguCui}/gas/predictions"
		const val FAVOURITE_AGU = "/{aguCui}/favourite"
		const val ACTIVE_AGU = "/{aguCui}/active"
		const val CONTACT = "/{aguCui}/contact"
		const val CONTACT_BY_ID = "/{aguCui}/contact/{contactId}"
		const val TANK = "/{aguCui}/tank"
		const val TANK_BY_ID = "/{aguCui}/tank/{tankNumber}"
		const val LEVELS = "/{aguCui}/levels"
		const val NOTES = "/{aguCui}/notes"

		fun byID(id: String) = UriTemplate(ROOT + BY_ID).expand(id)
		fun register() = URI(CREATE)
		fun temperature(cui: String) = UriTemplate(ROOT + TEMPERATURE).expand(cui)
		fun dailyGasLevels(cui: String) = UriTemplate(ROOT + DAILY_GAS_LEVELS).expand(cui)
		fun hourlyGasLevels(cui: String) = UriTemplate(ROOT + HOURLY_GAS_LEVELS).expand(cui)
		fun predictionGasLevels(cui: String) = UriTemplate(ROOT + PREDICTION_GAS_LEVELS).expand(cui)
		fun putFavouriteAGUs(cui: String) = UriTemplate(ROOT + FAVOURITE_AGU).expand(cui)
		fun putActiveAGUs(cui: String) = UriTemplate(ROOT + ACTIVE_AGU).expand(cui)
		fun contact(cui: String) = UriTemplate(ROOT + CONTACT).expand(cui)
		fun contactByID(cui: String, id: Int) = UriTemplate(ROOT + CONTACT_BY_ID).expand(cui, id)
		fun tank(cui: String) = UriTemplate(ROOT + TANK).expand(cui)
		fun tankByID(cui: String, id: Int) = UriTemplate(ROOT + TANK_BY_ID).expand(cui, id)
		fun levels(cui: String) = UriTemplate(ROOT + LEVELS).expand(cui)
		fun notes(cui: String) = UriTemplate(ROOT + NOTES).expand(cui)
	}

	/**
	 * Contains the URIs for the transport company endpoints
	 */
	object TransportCompany {
		const val ROOT = "$PREFIX/transport-companies"
		const val BY_ID = "/{transportCompanyId}"
		const val ALL_BY_CUI = "/agu/{aguCui}"
		const val BY_CUI_AND_ID = "/agu/{aguCui}/{transportCompanyId}"

		fun byID(id: Int) = UriTemplate(ROOT + BY_ID).expand(id)
		fun allByCUI(cui: String) = UriTemplate(ROOT + ALL_BY_CUI).expand(cui)
		fun byCUIAndID(cui: String, id: Int) = UriTemplate(ROOT + BY_CUI_AND_ID).expand(cui, id)
	}

	/**
	 * Contains the URIs for the DNO endpoints
	 */
	object Dno {
		const val ROOT = "$PREFIX/dnos"
		const val BY_ID = "/{dnoId}"

		fun byID(id: Int) = UriTemplate(ROOT + BY_ID).expand(id)
	}
}
