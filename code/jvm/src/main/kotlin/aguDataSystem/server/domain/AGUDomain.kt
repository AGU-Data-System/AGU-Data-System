package aguDataSystem.server.domain

import aguDataSystem.utils.Either
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

@Component
class AGUDomain {

	companion object {

		private val cuiRegex = Regex("^PT[0-9]{16}[A-Z]{2}$")
		private val phoneRegex = Regex("^[0-9]{9}$")
		private val contactTypeRegex = Regex("^(LOGISTIC|EMERGENCY)$")
		private const val TEMPERATURE_URI_TEMPLATE = "https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&daily=temperature_2m_max,temperature_2m_min&timezone=Europe%2FLondon&forecast_days=10"
		private const val FETCHER_URL = "http://localhost:8080/api/provider" //TODO: Maybe add this dinamically from ENV variables and from docker
	}

	private val client = HttpClient.newHttpClient()
	private val jsonFormatter = Json { prettyPrint = true }
	/**
	 * Generates the temperature URL for the given coordinates
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @return the generated URL
	 */
	fun generateTemperatureUrl(latitude: Double, longitude: Double): String {
		return TEMPERATURE_URI_TEMPLATE.replace("{latitude}", latitude.toString()).replace("{longitude}", longitude.toString())
	}

	/**
	 * Checks if the given CUI is valid
	 * @param cui the CUI to check
	 * @return true if the CUI is valid, false otherwise
	 */
	fun isCUIValid(cui: String): Boolean = cuiRegex.matches(cui)

	/**
	 * Checks if the given phone number is valid
	 * @param phone the phone number to check
	 * @return true if the phone number is valid, false otherwise
	 */
	fun isPhoneValid(phone: String): Boolean = phoneRegex.matches(phone)

	/**
	 * Checks if the given contact type is valid
	 * @param type the contact type to check
	 * @return true if the contact type is valid, false otherwise
	 */
	fun isContactTypeValid(type: String): Boolean = contactTypeRegex.matches(type.uppercase())

	/**
	 * Checks if a percentage is valid
	 * @param percentage the percentage to check
	 * @return true if the percentage is valid, false otherwise
	 */
	fun isPercentageValid(percentage: Int): Boolean = percentage in 0..100

	/**
	 * Checks if a Latitude is valid
	 * @param latitude the latitude to check
	 * @return true if the latitude is valid, false otherwise
	 */
	fun isLatitudeValid(latitude: Double): Boolean = latitude in -90.0..90.0

	/**
	 * Checks if a Longitude is valid
	 * @param longitude the longitude to check
	 * @return true if the longitude is valid, false otherwise
	 */
	fun isLongitudeValid(longitude: Double): Boolean = longitude in -180.0..180.0

	/**
	 * Checks if the levels are valid
	 * @param minLevel the minimum level
	 * @param maxLevel the maximum level
	 * @param criticalLevel the critical level
	 * @return true if the levels are valid, false otherwise
	 */
	fun areLevelsValid(minLevel: Int, maxLevel: Int, criticalLevel: Int): Boolean = minLevel in criticalLevel..maxLevel

	/**
	 * Sends a POST request to the fetcher to add a provider
	 * @param providerInput the provider input
	 * @return the result of the request (Left is the status code of the error in case of failure, Right is the ID of the created provider in case of success)
	 */
	fun addProviderRequest(providerInput: ProviderInput) : AddProviderResult {
		val request = HttpRequest.newBuilder()
			.uri(URI.create(FETCHER_URL))
			.header("Content-Type", "application/json")
			.POST(BodyPublishers.ofString(jsonFormatter.encodeToString(providerInput)))
			.build()

		return try {
			println("Sending POST request to $FETCHER_URL")
			println("Request body: ${jsonFormatter.encodeToString(providerInput)}")
			val response = client.send(request, BodyHandlers.ofString())

			println("Response status code: ${response.statusCode()}")
			println("Response body: ${response.body()}")

			if (response.statusCode() == 201){ // Created the provider
				val providerId = response.body().toInt()
				Either.Right(providerId)
			} else {
				Either.Left(response.statusCode())
			}
		} catch (e: Exception) {
			println("Error sending POST request: ${e.message}")
			Either.Left(500)
		}
	}
}

/**
 * The result of adding a provider
 * Left is the status code of the error in case of failure
 * Right is the ID of the created provider in case of success
 */
typealias AddProviderResult = Either<Int, Int>