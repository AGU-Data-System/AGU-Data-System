package aguDataSystem.server.domain.agu

import aguDataSystem.server.Environment
import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.contact.ContactType
import aguDataSystem.server.domain.provider.ProviderInput
import aguDataSystem.utils.Either
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class AGUDomain {

	companion object {

		private val logger = LoggerFactory.getLogger(AGUDomain::class.java)

		val cuiRegex = Regex("^PT[0-9]{16}[A-Z]{2}$")
		val phoneRegex = Regex("^[0-9]{9}$")
		val contactTypeRegex = Regex("^(LOGISTIC|EMERGENCY)$")

		private const val PROVIDER_CONTENT_TYPE = "application/json"
		private const val TEMPERATURE_URI_TEMPLATE =
			"https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&daily=temperature_2m_max,temperature_2m_min&timezone=Europe%2FLondon&forecast_days=10"
		private val FETCHER_URL: String = Environment.getFetcherUrl()
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
		return TEMPERATURE_URI_TEMPLATE
			.replace("{latitude}", latitude.toString())
			.replace("{longitude}", longitude.toString())
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
	fun isContactTypeValid(type: String): Boolean = ContactType.entries.any { it.name == type.uppercase() }

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
	private fun isLatitudeValid(latitude: Double): Boolean = latitude in -90.0..90.0

	/**
	 * Checks if a Longitude is valid
	 * @param longitude the longitude to check
	 * @return true if the longitude is valid, false otherwise
	 */
	private fun isLongitudeValid(longitude: Double): Boolean = longitude in -180.0..180.0

	/**
	 * Checks if the levels are valid
	 *
	 * @param levels the levels to check
	 * @return true if the levels are valid, false otherwise
	 */
	fun areLevelsValid(levels: GasLevels): Boolean = levels.min in levels.critical..levels.max

	/**
	 * Checks if the coordinates are valid
	 * @param latitude the latitude to check
	 * @param longitude the longitude to check
	 * @return true if the coordinates are valid, false otherwise
	 */
	fun areCoordinatesValid(latitude: Double, longitude: Double): Boolean {
		return isLatitudeValid(latitude) && isLongitudeValid(longitude)
	}

	/**
	 * Sends a POST request to the fetcher to add a provider
	 * @param providerInput the provider input
	 * @return the result of the request (Left is the status code of the error in case of failure, Right is the ID of the created provider in case of success)
	 */
	fun addProviderRequest(providerInput: ProviderInput): AddProviderResult {
		val createURL = "$FETCHER_URL/provider"
		val request = HttpRequest.newBuilder()
			.uri(URI.create(createURL))
			.header("Content-Type", PROVIDER_CONTENT_TYPE)
			.POST(HttpRequest.BodyPublishers.ofString(jsonFormatter.encodeToString(providerInput)))
			.build()

		return try {

			logger.info("Sending POST request to {}", createURL)
			logger.info("Request body: {}", jsonFormatter.encodeToString(providerInput))
			val response = client.send(request, HttpResponse.BodyHandlers.ofString())

			logger.info("Create response status code: {}", response.statusCode())
			logger.info("Response body: {}", response.body())

			if (response.statusCode() == HttpStatus.CREATED.value()) { // Created the provider
				val providerId = response.body().filter { it.isDigit() }.toInt()
				Either.Right(providerId)
			} else {
				Either.Left(response.statusCode())
			}
		} catch (e: Exception) {
			logger.error("Error sending POST request: {}", e.message)
			logger.error("Error Stack Trace: {}", e.stackTraceToString())
			Either.Left(HttpStatus.INTERNAL_SERVER_ERROR.value())
		}
	}

	/**
	 * Sends a DELETE request to the fetcher to delete a provider
	 * @param providerId the ID of the provider to delete
	 * @return the result of the request (Left is the status code of the error in case of failure, Right is true in case of success)
	 */
	fun deleteProviderRequest(providerId: Int): DeleteProviderResult {
		val deleteUrl = "/provider/$FETCHER_URL/$providerId"

		val request = HttpRequest
			.newBuilder()
			.uri(URI.create(deleteUrl))
			.header("Content-Type", PROVIDER_CONTENT_TYPE)
			.DELETE()
			.build()

		return try {
			logger.info("Sending DELETE request to {}", deleteUrl)
			val response = client.send(request, HttpResponse.BodyHandlers.ofString())

			logger.info("Delete response status code: {}", response.statusCode())
			if (response.statusCode() == HttpStatus.OK.value()) {
				Either.Right(true)
			} else {
				Either.Left(response.statusCode())
			}
		} catch (e: Exception) {
			logger.error("Error sending DELETE request: {}", e.message)
			Either.Left(HttpStatus.INTERNAL_SERVER_ERROR.value())
		}
	}
}

/**
 * The result of adding a provider
 * Left is the status code of the error in case of failure
 * Right is the ID of the created provider in case of success
 */
typealias AddProviderResult = Either<Int, Int>

/**
 * The result of deleting a provider
 * Left is the status code of the error in case of failure
 * Right is true in case of success
 */
typealias DeleteProviderResult = Either<Int, Boolean>
