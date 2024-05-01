package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.domain.provider.GasProviderInput
import aguDataSystem.server.domain.provider.TemperatureProviderInput
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.GetAGUError
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
	 * Get an AGU by its CUI
	 *
	 * @param cui the CUI of the AGU
	 * @return the AGU
	 */
	fun getAGUById(cui: String): GetAGUResult {
		return transactionManager.run {
			val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetAGUError.AGUNotFound)

			success(agu)
		}
	}

	/**
	 * Create a new AGU
	 *
	 * @param creationAGU the AGU to create
	 * @return the created AGU
	 */
	fun createAGU(creationAGU: AGUCreationDTO): AGUCreationResult {
		isAGUDTOValid(creationAGU)?.let {
			return it
		}

		val aguBasicInfo = creationAGU.toAGUBasicInfo()

		val temperatureUrl =
			aguDomain.generateTemperatureUrl(aguBasicInfo.location.latitude, aguBasicInfo.location.longitude)

		val gasProviderInput = GasProviderInput(aguBasicInfo.name, aguBasicInfo.gasLevelUrl)
		val temperatureProviderInput = TemperatureProviderInput(aguBasicInfo.name, temperatureUrl)
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
			val dno =
				it.dnoRepository.getByName(aguBasicInfo.dnoName) ?: return@run failure(AGUCreationError.InvalidDNO)
			it.aguRepository.addAGU(aguBasicInfo, dno.id)
			aguBasicInfo.tanks.forEach { tank ->
				it.tankRepository.addTank(aguBasicInfo.cui, tank)
			}
			aguBasicInfo.contacts.forEach { contact ->
				it.contactRepository.addContact(aguBasicInfo.cui, contact)
			}
			it.providerRepository.addProvider(aguBasicInfo.cui, gasRes.getSuccessOrThrow(), AGUDomain.GAS_TYPE)
			it.providerRepository.addProvider(aguBasicInfo.cui, tempRes.getSuccessOrThrow(), AGUDomain.TEMPERATURE_TYPE)

			success(creationAGU.cui)
		}
	}

	/**
	 * Check if the AGU DTO is valid
	 *
	 * @param aguDTO the AGU DTO to check
	 * @return the result of the check
	 */
	private fun isAGUDTOValid(aguDTO: AGUCreationDTO): AGUCreationResult? {
		if (!aguDomain.isCUIValid(aguDTO.cui)) {
			return failure(AGUCreationError.InvalidCUI)
		}

		if (!(aguDomain.areCoordinatesValid(aguDTO.location.latitude, aguDTO.location.longitude))) {
			return failure(AGUCreationError.InvalidCoordinates)
		}

		ensureLevels(aguDTO.levels)?.let {
			return it
		}

		if (aguDTO.tanks.isEmpty()) {
			return failure(AGUCreationError.InvalidTank)
		}

		aguDTO.tanks.forEach { tank ->
			ensureLevels(tank.levels)?.let {
				return it
			}
		}

		aguDTO.contacts.forEach {
			if (!aguDomain.isPhoneValid(it.phone) || it.name.isEmpty()) {
				return failure(AGUCreationError.InvalidContact)
			}
			if (!aguDomain.isContactTypeValid(it.type)) {
				return failure(AGUCreationError.InvalidContactType)
			}
		}

		return null
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