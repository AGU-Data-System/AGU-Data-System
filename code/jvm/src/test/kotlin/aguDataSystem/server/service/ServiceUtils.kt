package aguDataSystem.server.service

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.company.DNOCreationDTO
import aguDataSystem.server.domain.company.TransportCompanyCreationDTO
import aguDataSystem.server.domain.contact.ContactCreationDTO
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.gasLevels.GasLevelsDTO
import aguDataSystem.server.domain.tank.Tank
import aguDataSystem.server.domain.tank.TankUpdateDTO

object ServiceUtils {

	val dummyDNODTO = DNOCreationDTO(
		name = "Test DNO",
		region = "Test Region"
	)

	val dummyGasLevels = GasLevels(
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

//	private val dummyEmergencyContact = ContactCreationDTO(
//		name = "dummyEmergencyContact",
//		phone = "987654321",
//		type = "emergency"
//	)

	val dummyTank = Tank(
		number = 1,
		levels = dummyGasLevels,
		capacity = 50,
		correctionFactor = 1.0
	)

	val updateTankDTO = TankUpdateDTO(
		minLevel = dummyGasLevels.min,
		maxLevel = dummyGasLevels.max,
		criticalLevel = dummyGasLevels.critical,
		capacity = 80,
		correctionFactor = 1.0
	)

	val dummyGasLevelsDTO = GasLevelsDTO(
		min = 30,
		max = 90,
		critical = 10
	)

	val dummyTransportCompany = TransportCompanyCreationDTO(
		name = "Transport Company"
	)

	val dummyAGUCreationDTO = AGUCreationDTO(
		cui = "PT6543210987654321AA",
		eic = "TEST-EIC",
		name = "Service Test AGU",
		levels = dummyGasLevels,
		loadVolume = 50,
		correctionFactor = -1.0,
		location = dummyLocation,
		dnoName = dummyDNODTO.name,
		gasLevelUrl = "https://jsonplaceholder.typicode.com/todos/1",
		image = ByteArray(0),
		contacts = emptyList(),
		tanks = listOf(dummyTank),
		transportCompanies = emptyList(),
		isFavourite = false,
		isActive = true,
		notes = null,
		training = null
	)
}