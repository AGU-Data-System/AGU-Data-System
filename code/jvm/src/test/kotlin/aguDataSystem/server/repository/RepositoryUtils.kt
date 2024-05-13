package aguDataSystem.server.repository

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.agu.AGUCreationInfo
import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.ContactType
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.measure.TemperatureMeasure
import java.time.LocalDateTime

/**
 * Utility object for repository tests double data
 */
object RepositoryUtils {

	val dummyGasMeasures = List(10) {
		GasMeasure(
			timestamp = LocalDateTime.now().truncateNanos(),
			predictionFor = LocalDateTime.now().plusDays(it.toLong()).truncateNanos(),
			level = 50 - (it * 2)
		)
	}.reversed()

	val dummyTemperatureMeasures = List(10) {
		TemperatureMeasure(
			timestamp = LocalDateTime.now().truncateNanos(),
			predictionFor = LocalDateTime.now().plusDays(it.toLong()).truncateNanos(),
			min = it,
			max = 10 + it
		)
	}.reversed()

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

	val dummyTank = Tank(
		number = 0,
		levels = dummyGasLevels,
		loadVolume = 50,
		capacity = 100,
		correctionFactor = 0.0
	)

	private val dummyLocation = Location(
		latitude = 40.7128,
		longitude = 74.0060,
		name = "New York"
	)

	const val DUMMY_DNO_NAME = "TEST_DNO"

	val dummyAGU = AGUCreationInfo(
		cui = "PT1234567890123456XX",
		name = "Test AGU",
		levels = dummyGasLevels,
		loadVolume = 50,
		location = dummyLocation,
		dnoName = DUMMY_DNO_NAME,
		isFavorite = false,
		notes = null,
		training = null,
		image = ByteArray(0),
		contacts = emptyList(),
		tanks = emptyList(),
		gasLevelUrl = "http://localhost:8081/api/agu/PT1234567890123456XX/gasLevel",
	)

	/**
	 * Truncates the nanoseconds of a [LocalDateTime] to 0
	 *
	 * @receiver The [LocalDateTime] to truncate
	 * @return The truncated [LocalDateTime]
	 */
	private fun LocalDateTime.truncateNanos(): LocalDateTime {
		return this.withNano(0)
	}
}
