package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.domain.agu.AddProviderResult
import aguDataSystem.server.domain.provider.ProviderInput
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.server.service.errors.agu.GetMeasuresError
import aguDataSystem.utils.failure
import aguDataSystem.utils.getSuccessOrThrow
import aguDataSystem.utils.isFailure
import aguDataSystem.utils.isSuccess
import aguDataSystem.utils.success
import java.time.LocalDate
import java.time.LocalTime
import org.slf4j.LoggerFactory
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
	 * Get all AGUs
	 *
	 * @return List of the basic information of all AGUs
	 */
	fun getAGUsBasicInfo(): List<AGUBasicInfo> {
		return transactionManager.run {
			logger.info("Getting all AGUs from the database")

			val agus = it.aguRepository.getAGUsBasicInfo()

			logger.info("Retrieved {} AGUs from the database", agus.size)

			agus
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
		logger.info("Creation of AGU with CUI: {}, is valid", creationAGU.cui)

		val aguCreationInfo = creationAGU.toAGUCreationInfo()

		val temperatureUrl =
			aguDomain.generateTemperatureUrl(aguCreationInfo.location.latitude, aguCreationInfo.location.longitude)

		val gasProviderInput = ProviderInput(aguCreationInfo.name, aguCreationInfo.gasLevelUrl, ProviderType.GAS)
		val temperatureProviderInput = ProviderInput(aguCreationInfo.name, temperatureUrl, ProviderType.TEMPERATURE)

		logger.info("Adding Gas provider for AGU with CUI: {}", creationAGU.cui)
		val gasRes = aguDomain.addProviderRequest(gasProviderInput)

		logger.info("Adding Temperature provider for AGU with CUI: {}", creationAGU.cui)
		val tempRes = aguDomain.addProviderRequest(temperatureProviderInput)

		if (gasRes.isFailure() || tempRes.isFailure()) {
			logger.error("Failed to add providers for AGU with CUI: {}", creationAGU.cui)
			return deleteSuccessProviders(gasRes, tempRes)
		}

		return try {
			transactionManager.run {
				val dno =
					it.dnoRepository.getByName(aguCreationInfo.dnoName)
						?: return@run failure(AGUCreationError.InvalidDNO)

				it.aguRepository.addAGU(aguCreationInfo, dno.id)
				logger.info("AGU with CUI: {} added to the database", creationAGU.cui)

				aguCreationInfo.tanks.forEach { tank ->
					it.tankRepository.addTank(aguCreationInfo.cui, tank)
				}
				logger.info("Tanks added to AGU with CUI: {}", creationAGU.cui)

				aguCreationInfo.contacts.forEach { contact ->
					it.contactRepository.addContact(aguCreationInfo.cui, contact)
				}
				logger.info("Contacts added to AGU with CUI: {}", creationAGU.cui)

				it.providerRepository.addProvider(aguCreationInfo.cui, gasRes.getSuccessOrThrow(), ProviderType.GAS)
				logger.info("Gas provider added to AGU with CUI: {}", creationAGU.cui)

				it.providerRepository.addProvider(
					aguCreationInfo.cui,
					tempRes.getSuccessOrThrow(),
					ProviderType.TEMPERATURE
				)
				logger.info("Temperature provider added to AGU with CUI: {}", creationAGU.cui)

				success(creationAGU.cui)
			}
		} catch (e: Exception) {
			logger.error("Failed to add AGU with CUI: {}", creationAGU.cui)
			logger.error("Failed with message: {}", e.message)
			logger.error("Failed with stack trace: {}", e.stackTrace)
			deleteSuccessProviders(gasRes, tempRes)
			throw e
		}
	}

	/**
	 * Get an AGU by its CUI
	 *
	 * @param cui the CUI of the AGU
	 * @return the AGU
	 */
	fun getAGUById(cui: String): GetAGUResult {
		return transactionManager.run {
			logger.info("Getting AGU by CUI: {} from the database", cui)
			val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetAGUError.AGUNotFound)

			logger.info("Retrieved AGU by CUI from the database")
			success(agu)
		}
	}

	/**
	 * Get the temperature measures of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param days the number of days to get the temperature levels for
	 * @return the temperature levels
	 */
	fun getTemperatureMeasures(cui: String, days: Int): GetTemperatureMeasuresResult {
		return transactionManager.run {
			logger.info("Getting temperature measures for AGU with CUI: {} for the last {} days", cui, days)

			val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetMeasuresError.AGUNotFound)
			logger.info("AGU with CUI: {} found", cui)

			val provider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.TEMPERATURE)
				?: return@run failure(GetMeasuresError.ProviderNotFound)
			logger.info("Provider found for AGU with CUI: {}", cui)

			val levels = it.temperatureRepository.getTemperatureMeasures(provider.id, days)

			success(levels)
		}
	}

	/**
	 * Get the daily gas measures of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param days the number of days to get the gas levels for
	 * @param time the time to get the gas levels for
	 * @return the gas levels
	 */
	fun getDailyGasMeasures(cui: String, days: Int, time: LocalTime): GetGasMeasuresResult {
		return transactionManager.run {
			logger.info("Getting daily gas measures for AGU with CUI: {} for the last {} days", cui, days)
			val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetMeasuresError.AGUNotFound)
			logger.info("AGU with CUI: {} found", cui)

			val provider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
				?: return@run failure(GetMeasuresError.ProviderNotFound)
			logger.info("Provider found for AGU with CUI: {}", cui)

			val levels = it.gasRepository.getGasMeasures(provider.id, days, time)

			success(levels)
		}
	}

	/**
	 * Get the hourly gas measures of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param day the day to get the gas levels for
	 * @return the gas levels
	 */
	fun getHourlyGasMeasures(cui: String, day: LocalDate): GetGasMeasuresResult {
		return transactionManager.run {
			logger.info("Getting hourly gas measures for AGU with CUI: {} for the day: {}", cui, day)
			val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetMeasuresError.AGUNotFound)
			logger.info("AGU with CUI: {} found", cui)

			val provider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
				?: return@run failure(GetMeasuresError.ProviderNotFound)
			logger.info("Provider found for AGU with CUI: {}", cui)

			val levels = it.gasRepository.getGasMeasures(provider.id, day)

			success(levels)
		}
	}

	/**
	 * Get the prediction gas levels of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param days the number of days to get the gas levels for
	 * @param time the time to get the gas levels for
	 * @return the gas levels
	 */
	fun getPredictionGasLevels(cui: String, days: Int, time: LocalTime): GetGasMeasuresResult {
		return transactionManager.run {
			logger.info("Getting prediction gas levels for AGU with CUI: {} for the next {} days", cui, days)
			val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetMeasuresError.AGUNotFound)
			logger.info("AGU with CUI: {} found", cui)

			val provider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
				?: return@run failure(GetMeasuresError.ProviderNotFound)
			logger.info("Provider found for AGU with CUI: {}", cui)

			val levels = it.gasRepository.getPredictionGasMeasures(provider.id, days, time)

			success(levels)
		}
	}

	/**
	 * Gets the favourites AGUs and their basic information
	 *
	 * @return the list of favourite AGUs
	 */
	fun getFavouriteAGUs(): List<AGUBasicInfo> {
		return transactionManager.run {
			logger.info("Getting favourite AGUs from the database")

			val agus = it.aguRepository.getFavouriteAGUs()

			logger.info("Retrieved {} favourite AGUs from the database", agus.size)

			agus
		}
	}

	/**
	 * Updates the favourite status of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param isFavourite the new favourite status
	 * @return the updated AGU
	 */
	fun updateFavouriteState(cui: String, isFavourite: Boolean): UpdateFavouriteStateResult {
		return transactionManager.run {
			logger.info("Updating favourite status of AGU with CUI: {} to {}", cui, isFavourite)

			it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetAGUError.AGUNotFound)

			it.aguRepository.updateFavouriteState(cui, isFavourite)

			logger.info("Favourite status of AGU with CUI: {} updated to {}", cui, isFavourite)

			success(Unit)
		}
	}

	/**
	 * Update an AGU
	 *
	 * @param agu the AGU to update
	 * @return the updated AGU
	 */
	fun updateAGU(agu: AGUCreationDTO): UpdateAGUResult {
		//What do we want to update? just AGU info, or also tanks, or contacts, or DNO, or providers?
		//If we want to update everything, we need to check if the new info is valid, what is different form the old info
		//and update everything
		//this is specially hard for the providers because we have no info about what is the URL of the provider to check if it has changed,
		// for that we need to send a request to the fetcher
		//slowing down the update process, so i think we should have an update for each part
		//TODO()
		return success(Unit)
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

	/**
	 * Delete the providers that were successfully added
	 *
	 * @param gasRes the result of the gas provider
	 * @param tempRes the result of the temperature provider
	 */
	private fun deleteSuccessProviders(gasRes: AddProviderResult, tempRes: AddProviderResult): AGUCreationResult {
		logger.info("Deleting providers that were previously successfully added")
		if (gasRes.isSuccess()) {
			aguDomain.deleteProviderRequest(gasRes.getSuccessOrThrow())
		}
		if (tempRes.isSuccess()) {
			aguDomain.deleteProviderRequest(tempRes.getSuccessOrThrow())
		}

		return failure(AGUCreationError.ProviderError)
	}

	companion object {
		private val logger = LoggerFactory.getLogger(AGUService::class.java)
	}
}
