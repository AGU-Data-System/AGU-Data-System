package aguDataSystem.server.http

import aguDataSystem.server.http.controllers.agu.models.input.agu.AGUCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.agu.NotesInputModel
import aguDataSystem.server.http.controllers.agu.models.input.contact.ContactCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.dno.DNOCreationInputModel
import aguDataSystem.server.http.controllers.agu.models.input.gasLevels.GasLevelsInputModel
import aguDataSystem.server.http.controllers.agu.models.input.tank.TankCreationInputModel

/**
 * Data for requests
 * TODO - needs to be tested with different models than input models
 */
object ControllerUtils {

	val dummyContactCreationInputModel = ContactCreationInputModel(
		name = "John Doe",
		phone = "123456789",
		type = "logistic"
	)

	val dummyNotesInputModel = NotesInputModel(
		notes = "This is a note"
	)

	val dummyDNOCreationInputModel = DNOCreationInputModel(
		name = "Test DNO",
		region = "Test Region"
	)

	val dummyGasLevelsInputModel = GasLevelsInputModel(
		min = 30,
		max = 90,
		critical = 10
	)

	val dummyTankCreationInputModel = TankCreationInputModel(
		number = 1,
		minLevel = dummyGasLevelsInputModel.min,
		maxLevel = dummyGasLevelsInputModel.max,
		criticalLevel = dummyGasLevelsInputModel.critical,
		loadVolume = 40.0,
		capacity = 50,
		correctionFactor = 1.0
	)

	val dummyAGUCreationInputModel = AGUCreationInputModel(
		cui = "PT1234567890123456XX",
		name = "Test AGU",
		minLevel = dummyGasLevelsInputModel.min,
		maxLevel = dummyGasLevelsInputModel.max,
		criticalLevel = dummyGasLevelsInputModel.critical,
		loadVolume = 40.0,
		latitude = 0.0,
		longitude = 0.0,
		locationName = "dummyLocation",
		dnoName = dummyDNOCreationInputModel,
		gasLevelUrl = "https://jsonplaceholder.typicode.com/todos/1",
		image = ByteArray(0),
		tanks = listOf(dummyTankCreationInputModel),
		contacts = listOf(dummyContactCreationInputModel),
		isFavorite = false,
		notes = dummyNotesInputModel.notes
	)
}