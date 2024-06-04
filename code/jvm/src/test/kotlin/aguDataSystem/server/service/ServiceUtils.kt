package aguDataSystem.server.service

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.contact.ContactCreationDTO
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.tank.Tank

object ServiceUtils {

	const val dummyDNOName = "dummyDNOName"

	private val dummyGasLevels = GasLevels(
		min = 30,
		max = 90,
		critical = 10
	)

	private val dummyLocation = Location(
		latitude = 0.0,
		longitude = 0.0,
		name = "dummyLocation"
	)

	val dummyLogisticContact = ContactCreationDTO(
		name = "dummyLogisticContact",
		phone = "123456789",
		type = "logistic"
	)

	private val dummyEmergencyContact = ContactCreationDTO(
		name = "dummyEmergencyContact",
		phone = "987654321",
		type = "emergency"
	)

	val dummyTank = Tank(
		number = 1,
		levels = dummyGasLevels,
		loadVolume = 40,
		capacity = 50,
		correctionFactor = 1.0
	)

	val dummyAGUCreationDTO = AGUCreationDTO(
		cui = "PT6543210987654321AA",
		name = "Service Test AGU",
		levels = dummyGasLevels,
		loadVolume = 40,
		location = dummyLocation,
		dnoName = dummyDNOName,
		gasLevelUrl = "https://jsonplaceholder.typicode.com/todos/1",
		image = ByteArray(0),
		contacts = emptyList(),
		tanks = emptyList(),
		isFavorite = false,
		notes = null,
		training = null
	)
}