package aguDataSystem.server.http

import aguDataSystem.server.http.controllers.agu.models.input.agu.AGUCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.contact.ContactCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.gasLevels.GasLevelsInputModel
import aguDataSystem.server.http.controllers.agu.models.input.tank.TankCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.tank.TankUpdateInputModel
import java.time.LocalDate
import java.time.LocalTime
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Requests
 */
object HTTPUtils {

	fun createAGURequest(client: WebTestClient, aguCreation: AGUCreationInputModel) =
		client.post()
			.uri("/create")
			.bodyValue(aguCreation)
			.exchange()
			.expectStatus().isCreated
			.expectBody()
			.returnResult()
			.responseBody

	fun getAGURequest(client: WebTestClient, aguId: String) =
		client.get()
			.uri("/$aguId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getAllAGUsRequest(client: WebTestClient) =
		client.get()
			.uri("/")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getTemperatureMeasuresRequest(client: WebTestClient, aguId: String, days: Int) =
		client.get()
			.uri("/$aguId/temperature?days=$days")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getDailyGasMeasuresRequest(client: WebTestClient, aguId: String, days: Int, time: LocalTime) =
		client.get()
			.uri("/$aguId/gas/daily?days=$days&time=$time")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getHourlyGasMeasuresRequest(client: WebTestClient, aguId: String, day: LocalDate) =
		client.get()
			.uri("/$aguId/gas/hourly?day=$day")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getPredictionGasMeasuresRequest(client: WebTestClient, aguId: String, days: Int, time: LocalTime) =
		client.get()
			.uri("/$aguId/gas/predictions?days=$days&time=$time")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun getFavoriteAGUsRequest(client: WebTestClient) =
		client.get()
			.uri("/favorites")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun updateFavouriteStateRequest(client: WebTestClient, aguId: String, isFavorite: Boolean) =
		client.put()
			.uri("/$aguId/favorite")
			.bodyValue(
				"isFavorite" to isFavorite
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun addContactRequest(client: WebTestClient, aguId: String, contactInputModel: ContactCreationInputModel) =
		client.put()
			.uri("/$aguId/contact")
			.bodyValue(
				"contact" to contactInputModel
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun deleteContactRequest(client: WebTestClient, aguId: String, contactId: String) =
		client.delete()
			.uri("/$aguId/contact/$contactId")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun addTankRequest(client: WebTestClient, aguId: String, tankInputModel: TankCreationInputModel) =
		client.put()
			.uri("/$aguId/tank")
			.bodyValue(
				"tank" to tankInputModel
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun updateTankRequest(client: WebTestClient, aguId: String, tankId: String, tankUpdateInputModel: TankUpdateInputModel) =
		client.put()
			.uri("/$aguId/tank/$tankId")
			.bodyValue(
				"tank" to tankUpdateInputModel
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun changeGasLevelsRequest(client: WebTestClient, aguId: String, tankId: String, gasLevelsInputModel: GasLevelsInputModel) =
		client.put()
			.uri("/$aguId/tank/$tankId/gas")
			.bodyValue(
				"gasLevels" to gasLevelsInputModel
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody

	fun changeNotesRequest(client: WebTestClient, aguId: String, tankId: String, notes: String) =
		client.put()
			.uri("/$aguId/tank/$tankId/notes")
			.bodyValue(
				"notes" to notes
			)
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.returnResult()
			.responseBody
}