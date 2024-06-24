package aguDataSystem.server.http

import aguDataSystem.server.http.models.request.agu.AGUCreateRequestModel
import aguDataSystem.server.http.models.request.contact.ContactCreationRequestModel
import aguDataSystem.server.http.models.request.dno.DNOCreationRequestModel
import aguDataSystem.server.http.models.request.gasLevels.GasLevelsRequestModel
import aguDataSystem.server.http.models.request.notes.NotesRequestModel
import aguDataSystem.server.http.models.request.tank.TankCreationRequestModel
import aguDataSystem.server.http.models.request.tank.TankUpdateRequestModel
import aguDataSystem.server.http.models.request.transportCompany.TransportCompanyRequestModel
import kotlin.math.pow

/**
 * Data for requests
 */
object ControllerUtils {

	private const val NUMBER_LENGTH = 16
	private const val PHONE_NUMBER_LENGTH = 9

	private val dummyContactCreationRequestModel = ContactCreationRequestModel(
		name = "John Doe",
		phone = "123456789",
		type = "logistic"
	)

	private val dummyNotesRequestModel = NotesRequestModel(
		notes = "This is a note"
	)

	private val dummyDNOCreationRequestModel = DNOCreationRequestModel(
		name = "Test DNO",
		region = "Test Region"
	)

	val dummyGasLevelsRequestModel = GasLevelsRequestModel(
		min = 30,
		max = 90,
		critical = 10
	)

	val dummyTankCreationRequestModel = TankCreationRequestModel(
		number = 1,
		minLevel = dummyGasLevelsRequestModel.min,
		maxLevel = dummyGasLevelsRequestModel.max,
		criticalLevel = dummyGasLevelsRequestModel.critical,
		capacity = 50,
		correctionFactor = 1.0
	)

	val dummyTankUpdateRequestModel = TankUpdateRequestModel(
		minLevel = 10,
		maxLevel = 90,
		criticalLevel = 5,
		capacity = 1000,
		correctionFactor = 1.05
	)

	private val dummyTransportCompanyCreationRequestModel = TransportCompanyRequestModel(
		name = "Test Transport Company"
	)

	private val dummyAGUCreationRequestModel = AGUCreateRequestModel(
		cui = "PT1234567890123456XX",
		eic = "TEST-EIC",
		name = "Test AGU",
		minLevel = dummyGasLevelsRequestModel.min,
		maxLevel = dummyGasLevelsRequestModel.max,
		criticalLevel = dummyGasLevelsRequestModel.critical,
		correctionFactor = -1.0,
		latitude = 0.0,
		longitude = 0.0,
		locationName = "dummyLocation",
		dnoName = newTestDNO().name,
		gasLevelUrl = "https://jsonplaceholder.typicode.com/todos/1",
		image = ByteArray(0),
		tanks = listOf(dummyTankCreationRequestModel),
		contacts = listOf(newTestContact()),
		transportCompanies = emptyList(),
		isActive = true,
		isFavourite = false,
		notes = dummyNotesRequestModel.notes
	)

	/**
	 * Generate a random number
	 */
	private fun generateRandomNumber(): Long {
		require(NUMBER_LENGTH > 0) { "Digit count must be greater than zero" }

		val minValue = 10.0.pow((NUMBER_LENGTH - 1).toDouble()).toLong()
		val maxValue = 10.0.pow(NUMBER_LENGTH.toDouble()).toLong() - 1

		return (minValue..maxValue).random()
	}

	/**
	 * Create a new test DNO
	 */
	fun newTestDNO() = dummyDNOCreationRequestModel.copy(name = "DNO Test ${generateRandomNumber()}")

	/**
	 * Creates a new test AGU
	 */
	fun newTestAGU(dnoName: String) = dummyAGUCreationRequestModel.copy(
		name = "AGU Test ${generateRandomNumber()}",
		cui = "PT${generateRandomNumber()}XX",
		eic = "TEST-EIC ${generateRandomNumber()}",
		dnoName = dnoName
	)

	/**
	 * Creates a test transport company with a randomized name
	 */
	fun newTestTransportCompany() = dummyTransportCompanyCreationRequestModel.copy(
		name = "Transport Company Test ${generateRandomNumber()}"
	)

	/**
	 * Creates a test contact with a randomized name and phone number
	 */
	fun newTestContact() = dummyContactCreationRequestModel.copy(
		name = "Contact Test ${generateRandomNumber()}",
		phone = generateRandomNumber().toString().take(PHONE_NUMBER_LENGTH),
	)

	/**
	 * Creates a test tank with a number incremented by 1
	 */
	fun newTestTank() = dummyTankCreationRequestModel.copy(
		number = dummyTankCreationRequestModel.number + 1
	)
}