package aguDataSystem.server.service

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class FetchService {

	private val baseURL = "http://localhost:8080/api/provider"

	/**
	 * Makes a request to the given URL.
	 *
	 * @param id the id of the provider to fetch from
	 * @return The response body
	 */
	fun fetch(id: Int): Response {
		val url = "$baseURL/$id"
		val client = HttpClient.newHttpClient()
		val request = HttpRequest.newBuilder()
			.uri(URI.create(url)) // TODO needs begin date
			.GET()
			.build()
		try {
			val response = client.send(request, HttpResponse.BodyHandlers.ofString())
			return Response(response.statusCode(), response.body())
		} catch (e: Exception) {
			logger.error("Error fetching data from provider with url: {} and exception:", url, e)
			return Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.message.toString())
		}
	}

	/**
	 * Represents a response from a request.
	 *
	 * @property statusCode The status code of the response
	 * @property body The body of the response
	 */
	data class Response(val statusCode: Int, val body: String)

	companion object {
		private val logger = LoggerFactory.getLogger(FetchService::class.java)
	}

}