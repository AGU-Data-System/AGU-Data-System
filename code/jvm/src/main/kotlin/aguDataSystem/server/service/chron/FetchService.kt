package aguDataSystem.server.service.chron

import aguDataSystem.server.Environment
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.measure.Measure
import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.domain.measure.toGasReadings
import aguDataSystem.server.domain.measure.toTemperatureMeasures
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.chron.models.GasDataItem
import aguDataSystem.server.service.chron.models.ProviderResponseModel
import aguDataSystem.server.service.chron.models.TemperatureData
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

/**
 * Service for fetching data from a provider
 *
 * @property transactionManager The transaction manager
 */
@Service
class FetchService(
	private val transactionManager: TransactionManager
) {

	private val pageSize = Int.MAX_VALUE

	/**
	 * Fetches the data from the provider and saves it to the database.
	 *
	 * @param provider The provider to fetch from
	 * @param since The time to fetch data from
	 */
	fun fetchAndSave(provider: Provider, since: LocalDateTime) {
		val providerURL =
			Environment.getFetcherUrl() + "/provider/${provider.id}?beginDate=${since.plusSeconds(1)}&size=$pageSize"
		logger.info("Fetching data from provider: {}", provider.id)

		val response = fetch(url = providerURL)
		logger.info("Fetched data from provider: {} with status code: {}", provider.id, response.statusCode)
		if (response.statusCode != HttpStatus.OK.value()) return

		val data = response.body.deserialize(provider.getProviderType(), since)

		if (data.isEmpty()) {
			logger.info("No new data fetched from provider: {}", provider.id)
			return
		}

		transactionManager.run {
			val lastFetched = data.maxOf { data -> data.timestamp }
			it.providerRepository.updateLastFetch(provider.id, lastFetched)
			logger.info("Provider: {} - last fetch TimeStamp updated: {} to database", provider.id, lastFetched)

			logger.info("Saving data from provider: {} to database", provider.id)
			when (provider.getProviderType()) {
				ProviderType.TEMPERATURE ->
					it.temperatureRepository.addTemperatureMeasuresToProvider(
						providerId = provider.id,
						temperatureMeasures = data.toTemperatureMeasures()
					)

				ProviderType.GAS -> it.gasRepository.addGasMeasuresToProvider(
					providerId = provider.id,
					gasMeasures = data.toGasReadings()
				)
			}
			logger.info("Saved data from provider: {} to database", provider.id)
		}
	}

	/**
	 * Makes a request to the given URL.
	 *
	 * @param url The URL to make the request to
	 * @return The response body
	 */
	fun fetch(url: String): Response {
		val client = HttpClient.newHttpClient()
		val request = HttpRequest.newBuilder()
			.uri(URI.create(url))
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
	 * Deserializes a string to a list of measures.
	 *
	 * @param lastFetch last fetch from the provider
	 * @param providerType The type of provider
	 * @return The list of measures
	 */
	fun String.deserialize(providerType: ProviderType, lastFetch: LocalDateTime): List<Measure> {
		return when (providerType) {
			ProviderType.TEMPERATURE -> this.mapToTemperatureMeasures(lastFetch)
			ProviderType.GAS -> this.mapToGasMeasures(lastFetch)
		}
	}

	/**
	 * Maps a JsonNode to a list of temperature measures
	 *
	 * @receiver String to deserialize
	 * @param lastFetch lastFetch from the temperature [Provider]
	 * @return The list of temperature measures
	 */
	fun String.mapToTemperatureMeasures(lastFetch: LocalDateTime): List<TemperatureMeasure> {
		val objectMapper = Json { ignoreUnknownKeys = true; prettyPrint = true }
		val providerResponse = objectMapper.decodeFromString<ProviderResponseModel>(this)

		if (LocalDateTime.parse(providerResponse.lastFetch) == lastFetch)
			return emptyList()

		val temperatureMeasures = mutableListOf<TemperatureMeasure>()

		for (item in providerResponse.dataList) {
			val fetchTimestamp = LocalDateTime.parse(item.fetchTime)

			val dailyData = objectMapper.decodeFromString<TemperatureData>(item.data).daily

			dailyData.time.forEachIndexed { index, time ->
				val beginningOfDay = LocalDate.parse(time)
				temperatureMeasures.add(
					TemperatureMeasure(
						timestamp = fetchTimestamp,
						predictionFor = beginningOfDay.atStartOfDay(),
						max = dailyData.max[index].roundToInt(),
						min = dailyData.min[index].roundToInt()
					)
				)
			}
		}
		return temperatureMeasures
	}

	/**
	 * Maps a JsonNode to a list of gas measures
	 *
	 * @receiver String to deserialize
	 * @param lastFetch lastFetch from the gas [Provider]
	 * @return The list of gas measures
	 */
	fun String.mapToGasMeasures(lastFetch: LocalDateTime): List<GasMeasure> {
		val objectMapper = Json { ignoreUnknownKeys = true; prettyPrint = true }
		val providerResponse = objectMapper.decodeFromString<ProviderResponseModel>(this)

		if (LocalDateTime.parse(providerResponse.lastFetch) == lastFetch)
			return emptyList()

		val gasMeasures = mutableListOf<GasMeasure>()

		for (item in providerResponse.dataList) {
			val fetchTimestamp = LocalDateTime.parse(item.fetchTime)

			objectMapper.parseToJsonElement(item.data).jsonArray.forEach { elem ->
				val gasData = objectMapper.decodeFromJsonElement<GasDataItem>(elem)
				if (gasData.name.startsWith(GasDataItem.TANK_LEVEL)) {
					if (gasData.value == null) return@forEach
					gasMeasures.add(
						GasMeasure(
							timestamp = fetchTimestamp,
							predictionFor = fetchTimestamp,
							level = gasData.value.toInt(),
							tankNumber = gasData.name.last().digitToIntOrNull() ?: 1,
						)
					)
				}
			}
		}
		return gasMeasures
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