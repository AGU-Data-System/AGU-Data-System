//package aguDataSystem.utils
//
//import kotlinx.coroutines.runBlocking
//import kotlinx.serialization.json.Json
//import org.jsoup.Jsoup
//import java.net.URI
//import java.net.http.HttpClient
//import java.net.http.HttpRequest
//import java.net.http.HttpResponse
//
//fun main() {
//    runBlocking {
//        val scraper = DataScraper()
//        scraper.fetchAndPostSynoptics()
//    }
//}
//
//class DataScraper {
//    private val fetcherProvidersUrl = "http://10.64.13.59:8080/api/providers"
//    private val client = HttpClient.newHttpClient()
//
//    fun fetchAndPostSynoptics() {
//
//        val doc = Jsoup.connect(sonorgasUrl).get()
//        val rows = doc.select("#list .synoptic-list .row-head")
//
//        sendGetRequest()
//    }
//
//    private fun sendGetRequest(providerId: Int? =null) {
//        val request = HttpRequest.newBuilder()
//            .uri(URI.create(fetcherProvidersUrl))
//            .header("Content-Type", "application/json")
//            .GET()
//            .build()
//
//        try {
//            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
//            println("Response status code: ${response.statusCode()}")
//            println("Response body: ${response.body()}")
//        } catch (e: Exception) {
//            println("Error sending POST request: ${e.message}")
//        }
//    }
//}
