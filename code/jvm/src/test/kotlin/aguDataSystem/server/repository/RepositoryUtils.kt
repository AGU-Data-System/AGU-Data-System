package aguDataSystem.server.repository

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGUCreationInfo
import aguDataSystem.server.domain.company.DNOCreationDTO
import aguDataSystem.server.domain.contact.ContactCreation
import aguDataSystem.server.domain.contact.ContactType
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.domain.tank.Tank
import aguDataSystem.server.domain.tank.TankUpdateInfo
import java.time.LocalDateTime

/**
 * Utility object for repository tests double data
 */
object RepositoryUtils {

	val dummyGasMeasures = List(10) {
		GasMeasure(
			timestamp = LocalDateTime.now().truncateNanos(),
			predictionFor = LocalDateTime.now().plusDays(it.toLong()).truncateNanos(),
			level = 50 - (it * 2),
			tankNumber = 1
		)
	}

	val dummyTemperatureMeasures = List(10) {
		TemperatureMeasure(
			timestamp = LocalDateTime.now().truncateNanos(),
			predictionFor = LocalDateTime.now().plusDays(it.toLong()).truncateNanos(),
			min = it,
			max = 10 + it
		)
	}

	val dummyLogisticContact = ContactCreation(
		name = "John Doe",
		phone = "123456789",
		type = ContactType.LOGISTIC
	)

	val dummyEmergencyContact = ContactCreation(
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
		number = 1,
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

	val dummyDNO = DNOCreationDTO(
		name = "Test DNO",
		region = "Test Region"
	)

	val dummyAGU = AGUCreationInfo(
		cui = "PT1234567890123456XX",
		name = "Test AGU",
		levels = dummyGasLevels,
		loadVolume = 50,
		location = dummyLocation,
		dno = dummyDNO,
		isFavorite = false,
		notes = null,
		training = null,
		image = ByteArray(0),
		contacts = emptyList(),
		tanks = emptyList(),
		gasLevelUrl = "https://jsonplaceholder.typicode.com/todos/1",
	)

	/**
	 * Truncates the nanoseconds of a [LocalDateTime] to 0
	 *
	 * @receiver The [LocalDateTime] to truncate
	 * @return The truncated [LocalDateTime]
	 */
	fun LocalDateTime.truncateNanos(): LocalDateTime {
		return this.withNano(0)
	}

	/**
	 * Converts a [Tank] to a [TankUpdateInfo]
	 *
	 * @receiver The [Tank] to convert
	 * @return The [TankUpdateInfo] with the same values as the [Tank]
	 */
	fun Tank.toUpdateInfo(): TankUpdateInfo {
		return TankUpdateInfo(
			levels = this.levels,
			loadVolume = this.loadVolume,
			capacity = this.capacity,
			correctionFactor = this.correctionFactor
		)
	}
}
