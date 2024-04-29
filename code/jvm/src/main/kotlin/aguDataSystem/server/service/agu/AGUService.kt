package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.AGUCreationDTO
import aguDataSystem.server.domain.AGUDomain
import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.GasProviderInput
import aguDataSystem.server.domain.TemperatureProviderInput
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.utils.failure
import org.springframework.stereotype.Service

/**
 * Service for managing the AGUs.
 *
 * @property transactionManager The transaction manager
 * @property aguDomain The AGU domain
 */
@Service
class AGUService(
	private val transactionManager: TransactionManager,
	val aguDomain: AGUDomain
) {

	/**
	 * Create a new AGU
	 *
	 * @param agu the AGU to create
	 * @return the created AGU
	 */
	fun createAGU(agu: AGUCreationDTO): AGUCreationResult {
		if (!aguDomain.isCUIValid(agu.cui)) {
			return failure(AGUCreationError.InvalidCUI)
		}

		if (!(aguDomain.isLatitudeValid(agu.location.latitude) && aguDomain.isLongitudeValid(agu.location.longitude))) {
			return failure(AGUCreationError.InvalidCoordinates)
		}

		ensureLevels(agu.levels)?.let {
			return it
		}

		if (agu.tanks.isEmpty()) {
			return failure(AGUCreationError.InvalidTank)
		}

		agu.tanks.forEach { tank ->
			ensureLevels(tank.levels)?.let {
				return it
			}
		}

		agu.contacts.forEach {
			if (!aguDomain.isPhoneValid(it.phone) || it.name.isEmpty()) {
				return failure(AGUCreationError.InvalidContact)
			}
			if (!aguDomain.isContactTypeValid(it.type)) {
				return failure(AGUCreationError.InvalidContactType)
			}
		}

		val temperatureUrl = aguDomain.generateTemperatureUrl(agu.location.latitude, agu.location.longitude)

		val gasProviderInput = GasProviderInput(agu.name, agu.gasLevelUrl)
		val gasRes = aguDomain.addProviderRequest(gasProviderInput)
		val temperatureProviderInput = TemperatureProviderInput(agu.name, temperatureUrl)
		val tempRes = aguDomain.addProviderRequest(temperatureProviderInput)
		//TODO: Handle the result of the requests
		return transactionManager.run {
			//verify DNO exists in the database
			//create AGU
			//create tanks
			//create contacts

			//create providers with returned id from the fetcher
			//TODO: return the ID (CUI) of the created AGU
			TODO()
		}
	}

	/**
	 * Ensure the levels are valid
	 *
	 * @param levels the levels to check
	 * @return the result of the check
	 */
	private fun ensureLevels(levels: GasLevels): AGUCreationResult? {
		if (!aguDomain.isPercentageValid(levels.min)) {
			return failure(AGUCreationError.InvalidMinLevel)
		}

		if (!aguDomain.isPercentageValid(levels.max)) {
			return failure(AGUCreationError.InvalidMaxLevel)
		}

		if (!aguDomain.isPercentageValid(levels.critical)) {
			return failure(AGUCreationError.InvalidCriticalLevel)
		}

		if (!aguDomain.areLevelsValid(levels)) {
			return failure(AGUCreationError.InvalidLevels)
		}

		return null
	}
}