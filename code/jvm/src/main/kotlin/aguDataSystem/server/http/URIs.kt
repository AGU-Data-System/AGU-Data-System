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

		fun byID(id: String) = UriTemplate(ROOT + GET_BY_ID).expand(id)
		fun home() = URI(HOME)
		fun register() = URI(CREATE)
		fun temperature(id: String) = UriTemplate(ROOT + GET_TEMPERATURE).expand(id)
		fun dailyGasLevels(id: String) = UriTemplate(ROOT + GET_DAILY_GAS_LEVELS).expand(id)
		fun hourlyGasLevels(id: String) = UriTemplate(ROOT + GET_HOURLY_GAS_LEVELS).expand(id)
		fun predictionGasLevels(id: String) = UriTemplate(ROOT + GET_PREDICTION_GAS_LEVELS).expand(id)
	}

}