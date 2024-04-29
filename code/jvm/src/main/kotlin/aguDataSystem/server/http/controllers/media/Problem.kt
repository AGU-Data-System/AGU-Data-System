package aguDataSystem.server.http.controllers.media

import aguDataSystem.server.domain.AGUDomain
import java.net.URI
import org.springframework.http.ResponseEntity

/**
 * Represents a problem
 * @param typeUri The type of the problem
 * @param title The title of the problem
 */
class Problem(typeUri: URI, val title: String, val details : String? = null) {

	val type: String = typeUri.toASCIIString()

	companion object {
		private const val MEDIA_TYPE = "application/problem+json"
		private const val PROBLEM_BASE_URL =
			"https://github.com/AGU-Data-System/AGU-Data-System/blob/main/docs/problems/"

		/**
		 * Creates a response entity with the given status and problem
		 * @param problem The problem
		 * @return The response entity
		 */
		fun response(status: Int, problem: Problem) = ResponseEntity
			.status(status)
			.header("Content-Type", MEDIA_TYPE)
			.body<Any>(problem)

		val InvalidCUI = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-cui"),
			title = "Invalid CUI.",
			details = "The CUI must be in the format " + AGUDomain.cuiRegex.pattern
		)

		val InvalidContact = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-contact"),
			title = "Invalid Contact.",
			details = "The contact must have a name and a phone number in the format " + AGUDomain.phoneRegex.pattern
		)

		val InvalidContactType = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-contact-type"),
			title = "Invalid Contact Type.",
			details = "The contact type must be " + AGUDomain.contactTypeRegex.pattern
		)

		val InvalidCoordinates = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-coordinates"),
			title = "Invalid Coordinates.",
			details = "The coordinates must be valid."
		)

		val InvalidTank = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-tank"),
			title = "Invalid Tank.",
			details = "The tank must have a name and a volume."
		)

		val InvalidLevels = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-levels"),
			title = "Invalid Levels.",
			details = "The levels must be valid."
		)

		val InvalidLoadVolume = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-load-volume"),
			title = "Invalid Load Volume.",
			details = "The load volume must be valid."
		)

		val InvalidMinLevel = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-min-level"),
			title = "Invalid Min Level.",
			details = "The min level must be valid."
		)

		val InvalidMaxLevel = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-max-level"),
			title = "Invalid Max Level.",
			details = "The max level must be valid."
		)

		val InvalidCriticalLevel = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-critical-level"),
			title = "Invalid Critical Level.",
			details = "The critical level must be valid."
		)

		val InvalidDNO = Problem(
			typeUri = URI(PROBLEM_BASE_URL + "invalid-dno"),
			title = "Invalid DNO.",
			details = "The DNO must have a name."
		)
	}
}