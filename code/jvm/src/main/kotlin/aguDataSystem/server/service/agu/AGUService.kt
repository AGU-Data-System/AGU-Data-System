package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.domain.agu.AddProviderResult
import aguDataSystem.server.domain.contact.ContactCreationDTO
import aguDataSystem.server.domain.gasLevels.GasLevels
import aguDataSystem.server.domain.gasLevels.GasLevelsDTO
import aguDataSystem.server.domain.provider.ProviderInput
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.domain.tank.Tank
import aguDataSystem.server.domain.tank.TankUpdateDTO
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.chron.ChronService
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.DeleteAGUError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.server.service.errors.agu.update.UpdateActiveStateError
import aguDataSystem.server.service.errors.agu.update.UpdateFavouriteStateError
import aguDataSystem.server.service.errors.agu.update.UpdateGasLevelsError
import aguDataSystem.server.service.errors.agu.update.UpdateNotesError
import aguDataSystem.server.service.errors.contact.AddContactError
import aguDataSystem.server.service.errors.contact.DeleteContactError
import aguDataSystem.server.service.errors.measure.GetMeasuresError
import aguDataSystem.server.service.errors.tank.AddTankError
import aguDataSystem.server.service.errors.tank.DeleteTankError
import aguDataSystem.server.service.errors.tank.UpdateTankError
import aguDataSystem.server.service.prediction.PredictionService
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
	private val aguDomain: AGUDomain,
	private val scheduleChron: ChronService,
	private val predictionService: PredictionService
) {

	/**
	 * TODO
	 */
	fun trainAGUs() {
		logger.info("Training AGUs")
		predictionService.trainAGUs()
		logger.info("AGUs trained")
	}

	/**
	 * Get all AGUs
	 *
	 * @return List of the basic information for all AGUs
	 */
	fun getAGUsBasicInfo(): List<AGUBasicInfo> {
		trainAGUs() // for tests
		return transactionManager.run {
			logger.info("Getting all AGUs from the database")

			val aguList = it.aguRepository.getAGUsBasicInfo()

			logger.info("Retrieved: {} AGUs from the database", aguList.size)

			aguList
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

		logger.info(
			"Adding Gas provider for AGU with CUI: {}, and with name: {}",
			creationAGU.cui,
			gasProviderInput.name
		)
		val gasRes = aguDomain.addProviderRequest(gasProviderInput)

		logger.info(
			"Adding Temperature provider for AGU with CUI: {}, and with name: {}",
			creationAGU.cui,
			temperatureProviderInput.name
		)
		val tempRes = aguDomain.addProviderRequest(temperatureProviderInput)

		if (gasRes.isFailure() || tempRes.isFailure()) {
			logger.error("Failed to add providers for AGU with CUI: {}", creationAGU.cui)
			return deleteSuccessProviders(gasRes, tempRes)
		}

		val result = try {
			transactionManager.run {

				if (getAGUById(creationAGU.cui).isSuccess())
					return@run failure(AGUCreationError.AGUAlreadyExists)

				if (getAGUsBasicInfo().any { aguBasicInfo -> aguBasicInfo.eic == creationAGU.eic })
					return@run failure(AGUCreationError.AGUAlreadyExists)

				if (it.aguRepository.getCUIByName(creationAGU.name) != null)
					return@run failure(AGUCreationError.AGUNameAlreadyExists)

				val dno = it.dnoRepository.getByName(aguCreationInfo.dnoName)
					?: return@run failure(AGUCreationError.DNONotFound)

				it.aguRepository.addAGU(aguCreationInfo, dno.id)
				logger.info("AGU basic info with CUI: {} added to the database", creationAGU.cui)

				aguCreationInfo.tanks.forEach { tank ->
					it.tankRepository.addTank(aguCreationInfo.cui, tank)
				}
				logger.info("Tanks added to AGU with CUI: {}", creationAGU.cui)

				aguCreationInfo.contacts.forEach { contact ->
					it.contactRepository.addContact(aguCreationInfo.cui, contact)
				}
				logger.info("Contacts added to AGU with CUI: {}", creationAGU.cui)

				aguCreationInfo.transportCompanies.forEach { company ->
					val transportCompany = it.transportCompanyRepository.getTransportCompanyByName(company)
						?: return@run failure(AGUCreationError.TransportCompanyNotFound)
					it.transportCompanyRepository.addTransportCompanyToAGU(aguCreationInfo.cui, transportCompany.id)
				}

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
		if (result.isFailure()) {
			logger.error("Failed to add AGU with CUI: {}", creationAGU.cui)
			deleteSuccessProviders(gasRes, tempRes)
			return result
		}
		logger.info("AGU with CUI: {} added successfully", creationAGU.cui)

		logger.info("Scheduling chron tasks for AGU with CUI: {}", creationAGU.cui)
		val providers = transactionManager.run {
			if (result.isSuccess())
				it.providerRepository.getProviderByAGU(result.getSuccessOrThrow())
			else
				null
		} ?: return failure(AGUCreationError.ProviderError)
		providers.forEach { provider ->
			scheduleChron.scheduleChronTask(provider, provider.getProviderType().pollingFrequency)
		}
		logger.info("Chron tasks scheduled for AGU with CUI: {}", creationAGU.cui)

		return result
	}

	/**
	 * Get an AGU by its CUI
	 *
	 * @param cui the CUI of the AGU
	 * @return the AGU
	 */
	fun getAGUById(cui: String): GetAGUResult {

		if (!aguDomain.isCUIValid(cui)) return failure(GetAGUError.InvalidCUI)

		return transactionManager.run {
			logger.info("Getting AGU by CUI: {} from the database", cui)

			success(getFullAGU(cui) ?: return@run failure(GetAGUError.AGUNotFound))
		}
	}

	/**
	 * Delete an AGU by its CUI
	 *
	 * @param cui the CUI of the AGU
	 * @return the result of the deletion
	 */
	fun deleteAGU(cui: String): DeleteAGUResult {
		if (!aguDomain.isCUIValid(cui)) return failure(DeleteAGUError.InvalidCUI)

		return transactionManager.run {
			logger.info("Deleting AGU with CUI: {} from the database", cui)
			logger.info("Deleting AGU tanks from the database")
			val tanks = it.tankRepository.getAGUTanks(cui)
			tanks.forEach { tank ->
				it.gasRepository.deleteGasMeasuresByTank(cui, tank.number)
				it.temperatureRepository.deleteTemperatureMeasuresByTank(cui, tank.number)
				it.tankRepository.deleteTank(cui, tank.number)
			}

			logger.info("Deleting AGU contacts from the database")
			val contacts = it.contactRepository.getContactsByAGU(cui)
			contacts.forEach { contact ->
				it.contactRepository.deleteContact(cui, contact.id)
			}

			logger.info("Deleting AGU providers from the database")
			val providers = it.providerRepository.getProviderByAGU(cui)
			providers.forEach { provider ->
				it.providerRepository.deleteProviderById(provider.id, cui)
			}
			it.aguRepository.deleteAGU(cui)

			success(Unit)
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

		if (!aguDomain.isCUIValid(cui)) return failure(GetMeasuresError.InvalidCUI)

		if (days < 1) return failure(GetMeasuresError.InvalidDays)

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
		if (!aguDomain.isCUIValid(cui)) return failure(GetMeasuresError.InvalidCUI)

		if (days < 1) return failure(GetMeasuresError.InvalidDays)

		if (time.hour < 0 || time.hour > 23 || time.minute < 0 || time.minute > 59) {
			return failure(GetMeasuresError.InvalidTime)
		}

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
		if (!aguDomain.isCUIValid(cui)) return failure(GetMeasuresError.InvalidCUI)

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

		if (!aguDomain.isCUIValid(cui)) return failure(GetMeasuresError.InvalidCUI)

		if (days < 1) return failure(GetMeasuresError.InvalidDays)

		if (time.hour < 0 || time.hour > 23 || time.minute < 0 || time.minute > 59) {
			return failure(GetMeasuresError.InvalidTime)
		}

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
	 * Updates the favourite status of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param isFavourite the new favourite status
	 * @return the updated AGU
	 */
	fun updateFavouriteState(cui: String, isFavourite: Boolean): UpdateFavouriteStateResult {
		return transactionManager.run {
			logger.info("Updating favourite status of AGU with CUI: {} to {}", cui, isFavourite)

			it.aguRepository.getAGUByCUI(cui) ?: return@run failure(UpdateFavouriteStateError.AGUNotFound)

			it.aguRepository.updateFavouriteState(cui, isFavourite)

			logger.info("Favourite status of AGU with CUI: {} updated to {}", cui, isFavourite)

			success(getFullAGU(cui) ?: return@run failure(UpdateFavouriteStateError.AGUNotFound))
		}
	}

	/**
	 * Updates the active status of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param isActive the new active status
	 * @return the updated AGU
	 */
	fun updateActiveState(cui: String, isActive: Boolean): UpdateActiveStateResult {
		return transactionManager.run {
			logger.info("Updating active status of AGU with CUI: {} to {}", cui, isActive)

			it.aguRepository.getAGUByCUI(cui) ?: return@run failure(UpdateActiveStateError.AGUNotFound)

			it.aguRepository.updateActiveState(cui, isActive)

			logger.info("Active status of AGU with CUI: {} updated to {}", cui, isActive)
			// TODO something is off here
			success(getFullAGU(cui) ?: return@run failure(UpdateActiveStateError.AGUNotFound))
		}
	}

	/**
	 * Add a contact to an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param contact the contact to add
	 * @return the AGU with the added contact or an error
	 */
	fun addContact(cui: String, contact: ContactCreationDTO): AddContactResult {
		logger.info("Adding contact to AGU with CUI: {}", cui)

		if (!aguDomain.isPhoneValid(contact.phone) || contact.name.isEmpty()) {
			return failure(AddContactError.InvalidContact)
		}

		if (!aguDomain.isContactTypeValid(contact.type)) {
			return failure(AddContactError.InvalidContactType)
		}
		return transactionManager.run {

			it.aguRepository.getAGUByCUI(cui) ?: return@run failure(AddContactError.AGUNotFound)

			if (it.contactRepository.isContactStoredByPhoneNumberAndType(cui, contact.phone, contact.type)) {
				return@run failure(AddContactError.ContactAlreadyExists)
			}

			val contactId = it.contactRepository.addContact(cui, contact.toContactCreation())

			logger.info("Contact added to AGU with CUI: {}", cui)

			success(contactId)
		}
	}

	/**
	 * Delete a contact from an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param id the ID of the contact to delete
	 * @return the result of the deletion
	 */
	fun deleteContact(cui: String, id: Int): DeleteContactResult {
		return transactionManager.run {
			logger.info("Deleting contact with ID: {} from AGU with CUI: {}", id, cui)

			it.aguRepository.getAGUByCUI(cui) ?: return@run failure(DeleteContactError.AGUNotFound)

			it.contactRepository.deleteContact(cui, id)

			logger.info("Contact with ID: {} deleted from AGU with CUI: {}", id, cui)

			success(Unit)
		}
	}

	/**
	 * Adds a tank to an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param tank the tank to add
	 * @return the AGU with the added tank or an error
	 */
	fun addTank(cui: String, tank: Tank): AddTankResult {

		if (!aguDomain.areLevelsValid(tank.levels)) {
			return failure(AddTankError.InvalidLevels)
		}

		if (!aguDomain.isCapacityValid(tank.capacity)) {
			return failure(AddTankError.InvalidCapacity)
		}

		if (!aguDomain.isTankNumberValid(tank.number)) {
			return failure(AddTankError.InvalidTankNumber)
		}

		return transactionManager.run {
			logger.info("Adding tank to AGU with CUI: {}", cui)

			it.aguRepository.getAGUByCUI(cui) ?: return@run failure(AddTankError.AGUNotFound)

			it.tankRepository.getTankByNumber(cui, tank.number)?.let {
				return@run failure(AddTankError.TankAlreadyExists)
			}

			val tankNumber = it.tankRepository.addTank(cui, tank)

			logger.info("Tank added to AGU with CUI: {}", cui)

			success(tankNumber)
		}
	}

	/**
	 * Updates a tank of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param number the number of the tank to change
	 * @param tankDTO the new tank data
	 * @return the AGU with the changed tank or an error
	 */
	fun updateTank(cui: String, number: Int, tankDTO: TankUpdateDTO): UpdateTankResult {

		val tankUpdateInfo = tankDTO.toTankUpdateInfo()

		if (!aguDomain.areLevelsValid(tankUpdateInfo.levels)) {
			return failure(UpdateTankError.InvalidLevels)
		}

		if (!aguDomain.isCUIValid(cui)) {
			return failure(UpdateTankError.InvalidCUI)
		}

		if (!aguDomain.isCapacityValid(tankUpdateInfo.capacity)) {
			return failure(UpdateTankError.InvalidCapacity)
		}

		if (!aguDomain.isTankNumberValid(number)) {
			return failure(UpdateTankError.InvalidTankNumber)
		}

		return transactionManager.run {
			logger.info("Changing tank with number: {} from AGU with CUI: {}", number, cui)

			it.aguRepository.getAGUByCUI(cui) ?: return@run failure(UpdateTankError.AGUNotFound)

			if (it.tankRepository.getTankByNumber(cui, number) == null) {
				return@run failure(UpdateTankError.TankNotFound)
			}

			it.tankRepository.updateTank(cui, number, tankUpdateInfo)

			logger.info("Tank with number: {} changed from AGU with CUI: {}", number, cui)

			success(getFullAGU(cui) ?: return@run failure(UpdateTankError.AGUNotFound))
		}
	}

	/**
	 * Deletes a tank from an AGU
	 */
	fun deleteTank(cui: String, number: Int): DeleteTankResult {

		if (!aguDomain.isCUIValid(cui)) {
			return failure(DeleteTankError.InvalidCUI)
		}

		return transactionManager.run {
			logger.info("Deleting tank with number: {} from AGU with CUI: {}", number, cui)

			it.aguRepository.getAGUByCUI(cui) ?: return@run failure(DeleteTankError.AGUNotFound)

			it.tankRepository.deleteTank(cui, number)

			logger.info("Tank with number: {} deleted from AGU with CUI: {}", number, cui)

			success(Unit)
		}
	}

	/**
	 * Updates the gas levels of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param levelsDTO the new gas levels
	 * @return the AGU with the changed gas levels or an error
	 */
	fun updateGasLevels(cui: String, levelsDTO: GasLevelsDTO): UpdateGasLevelsResult {

		val levels = levelsDTO.toGasLevels()

		if (!aguDomain.areLevelsValid(levels)) {
			return failure(UpdateGasLevelsError.InvalidLevels)
		}

		return transactionManager.run {
			logger.info("Changing gas levels of AGU with CUI: {}", cui)

			if (it.aguRepository.getAGUByCUI(cui) == null) {
				return@run failure(UpdateGasLevelsError.AGUNotFound)
			}

			it.aguRepository.updateGasLevels(cui, levels)

			logger.info("Gas levels of AGU with CUI: {} changed", cui)

			success(getFullAGU(cui) ?: return@run failure(UpdateGasLevelsError.AGUNotFound))
		}
	}

	/**
	 * Updates the notes of an AGU
	 *
	 * @param cui the CUI of the AGU
	 * @param notes the new notes
	 * @return the AGU with the changed notes or an error
	 */
	fun updateNotes(cui: String, notes: String): UpdateNotesResult {
		return transactionManager.run {
			logger.info("Changing notes of AGU with CUI: {}", cui)

			if (it.aguRepository.getAGUByCUI(cui) == null) {
				return@run failure(UpdateNotesError.AGUNotFound)
			}

			it.aguRepository.updateNotes(cui, notes)

			logger.info("Notes of AGU with CUI: {} changed", cui)

			success(getFullAGU(cui) ?: return@run failure(UpdateNotesError.AGUNotFound))
		}
	}

	/**
	 * Private function to be used by the other service functions,
	 * to get the full AGU details by its CUI
	 *
	 * @param cui the CUI of the AGU
	 *
	 * @return the AGU with all its details
	 */
	private fun getFullAGU(cui: String): AGU? {
		return transactionManager.run {
			val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run null
			val tanks = it.tankRepository.getAGUTanks(cui)
			val contacts = it.contactRepository.getContactsByAGU(cui)
			val providers = it.providerRepository.getProviderByAGU(cui)
			val transportCompanies = it.transportCompanyRepository.getTransportCompaniesByAGU(cui)

			agu.copy(tanks = tanks, contacts = contacts, providers = providers, transportCompanies = transportCompanies)
		}
	}

	/**
	 * Check if the AGU DTO is valid
	 *
	 * @param aguDTO the AGU DTO to check
	 * @return the result of the check
	 */
	private fun isAGUDTOValid(aguDTO: AGUCreationDTO): AGUCreationResult? {
		if (aguDTO.name.isEmpty())
			return failure(AGUCreationError.InvalidName)

		if (!aguDomain.isCUIValid(aguDTO.cui)) {
			return failure(AGUCreationError.InvalidCUI)
		}

		if (!aguDomain.isEICValid(aguDTO.eic)) {
			return failure(AGUCreationError.InvalidEIC)
		}

		if (!(aguDomain.areCoordinatesValid(aguDTO.location.latitude, aguDTO.location.longitude))) {
			return failure(AGUCreationError.InvalidCoordinates)
		}

		if (!aguDomain.isLoadVolumeValid(aguDTO.loadVolume)) {
			return failure(AGUCreationError.InvalidLoadVolume)
		}

		ensureLevels(aguDTO.levels)?.let {
			return it
		}

		if (aguDTO.tanks.isEmpty()) {
			return failure(AGUCreationError.InvalidTank)
		}

		aguDTO.tanks.forEach { tank ->
			if (!aguDomain.isCapacityValid(tank.capacity)) {
				return failure(AGUCreationError.InvalidTank)
			}

			if (!aguDomain.isTankNumberValid(tank.number)) {
				return failure(AGUCreationError.InvalidTank)
			}

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
