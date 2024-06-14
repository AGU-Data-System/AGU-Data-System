package aguDataSystem.server.http

import aguDataSystem.server.http.models.request.agu.AGUCreateRequestModel
import aguDataSystem.server.http.models.request.contact.ContactCreationRequestModel
import aguDataSystem.server.http.models.request.dno.DNOCreationRequestModel
import aguDataSystem.server.http.models.request.gasLevels.GasLevelsRequestModel
import aguDataSystem.server.http.models.request.notes.NotesRequestModel
import aguDataSystem.server.http.models.request.tank.TankCreationRequestModel

/**
 * Data for requests
 */
object ControllerUtils {

	val dummyContactCreationRequestModel = ContactCreationRequestModel(
		name = "John Doe",
		phone = "123456789",
		type = "logistic"
	)

	val dummyNotesRequestModel = NotesRequestModel(
		notes = "This is a note"
	)

	val dummyDNOCreationRequestModel = DNOCreationRequestModel(
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
		loadVolume = 40.0,
		capacity = 50,
		correctionFactor = 1.0
	)

	val dummyTransportCompanyNames = listOf("Test Transport Company 1", "Test Transport Company 2")

	val dummyAGUCreationRequestModel = AGUCreateRequestModel(
		cui = "PT1234567890123456XX",
		eic = "TEST-EIC",
		name = "Test AGU",
		minLevel = dummyGasLevelsRequestModel.min,
		maxLevel = dummyGasLevelsRequestModel.max,
		criticalLevel = dummyGasLevelsRequestModel.critical,
		loadVolume = 40.0,
		correctionFactor = -1.0,
		latitude = 0.0,
		longitude = 0.0,
		locationName = "dummyLocation",
		dnoName = "Test DNO",
		gasLevelUrl = "https://jsonplaceholder.typicode.com/todos/1",
		image = ByteArray(0),
		tanks = listOf(dummyTankCreationRequestModel),
		contacts = listOf(dummyContactCreationRequestModel),
		transportCompanies = emptyList(),
		isFavourite = false,
		notes = dummyNotesRequestModel.notes
	)
}