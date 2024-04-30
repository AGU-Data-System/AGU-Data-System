package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.domain.provider.GasProviderInput
import aguDataSystem.server.domain.provider.TemperatureProviderInput
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.utils.failure
import aguDataSystem.utils.getSuccessOrThrow
import aguDataSystem.utils.isFailure
import aguDataSystem.utils.isSuccess
import aguDataSystem.utils.success
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
	 * @param creationAGU the AGU to create
	 * @return the created AGU
	 */
	fun createAGU(creationAGU: AGUCreationDTO): AGUCreationResult {
		if (!aguDomain.isCUIValid(creationAGU.cui)) {
			return failure(AGUCreationError.InvalidCUI)
		}

		if (!(aguDomain.isLatitudeValid(creationAGU.location.latitude) && aguDomain.isLongitudeValid(creationAGU.location.longitude))) {
			return failure(AGUCreationError.InvalidCoordinates)
		}

		ensureLevels(creationAGU.levels)?.let {
			return it
		}

		if (creationAGU.tanks.isEmpty()) {
			return failure(AGUCreationError.InvalidTank)
		}

		creationAGU.tanks.forEach { tank ->
			ensureLevels(tank.levels)?.let {
				return it
			}
		}

		creationAGU.contacts.forEach {
			if (!aguDomain.isPhoneValid(it.phone) || it.name.isEmpty()) {
				return failure(AGUCreationError.InvalidContact)
			}
			if (!aguDomain.isContactTypeValid(it.type)) {
				return failure(AGUCreationError.InvalidContactType)
			}
		}

		val temperatureUrl = aguDomain.generateTemperatureUrl(creationAGU.location.latitude, creationAGU.location.longitude)

		val gasProviderInput = GasProviderInput(creationAGU.name, creationAGU.gasLevelUrl)
		val temperatureProviderInput = TemperatureProviderInput(creationAGU.name, temperatureUrl)
		val gasRes = aguDomain.addProviderRequest(gasProviderInput)
		val tempRes = aguDomain.addProviderRequest(temperatureProviderInput)

		if (gasRes.isFailure() || tempRes.isFailure()) {
			if (gasRes.isSuccess()) {
				aguDomain.deleteProviderRequest(gasRes.getSuccessOrThrow())
			}
			if (tempRes.isSuccess()) {
				aguDomain.deleteProviderRequest(tempRes.getSuccessOrThrow())
			}
			return failure(AGUCreationError.ProviderError)
		}
		// TODO: Não sei o que fazer no caso do delete não funcionar

		return transactionManager.run {
			val dno = it.dnoRepository.getByName(creationAGU.dnoName) ?: return@run failure(AGUCreationError.InvalidDNO)
			it.aguRepository.addAGU(creationAGU, dno.id)
			creationAGU.tanks.forEach { tank ->
				it.tankRepository.addTank(creationAGU.cui, tank)
			}
			creationAGU.contacts.forEach { contact ->
				it.contactRepository.addContact(creationAGU.cui, contact)
			}
			it.providerRepository.addProvider(creationAGU.cui, gasRes.getSuccessOrThrow(), AGUDomain.GAS_TYPE)
			it.providerRepository.addProvider(creationAGU.cui, tempRes.getSuccessOrThrow(), AGUDomain.TEMPERATURE_TYPE)

			success(creationAGU.cui)
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