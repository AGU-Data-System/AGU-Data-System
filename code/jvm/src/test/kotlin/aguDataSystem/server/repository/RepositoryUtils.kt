package aguDataSystem.server.repository

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.ContactType

/**
 * Utility object for repository tests double data
 */
object RepositoryUtils {

	val dummyLogisticContact = Contact(
		name = "John Doe",
		phone = "123456789",
		type = ContactType.LOGISTIC
	)

	val dummyEmergencyContact = Contact(
		name = "Jane Doe",
		phone = "987654321",
		type = ContactType.EMERGENCY
	)

	private val dummyGasLevels = GasLevels(
		min = 20,
		max = 85,
		critical = 20
	)

	private val dummyTank = Tank(
		number = 0,
		levels = dummyGasLevels,
		loadVolume = 50,
		capacity = 100,
	)

	private val dummyLocation = Location(
		latitude = 40.7128,
		longitude = 74.0060,
		name = "New York"
	)

	const val DUMMY_DNO_NAME = "TEST_DNO"

	val dummyAGU = AGUBasicInfo(
		cui = "PT1234567890123456XX",
		name = "Test AGU",
		levels = dummyGasLevels,
		loadVolume = 50,
		location = dummyLocation,
		dnoName = DUMMY_DNO_NAME,
		isFavorite = false,
		notes = null,
		training = null,
		image = ByteArray(1) { 0.toByte() },
		contacts = listOf(dummyLogisticContact, dummyEmergencyContact),
		tanks = listOf(dummyTank),
		gasLevelUrl = "http://localhost:8081/api/agu/PT1234567890123456XX/gasLevel",
	)
}
