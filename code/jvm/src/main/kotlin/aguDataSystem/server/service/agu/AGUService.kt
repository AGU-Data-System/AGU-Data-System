package aguDataSystem.server.service.agu

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.agu.AGUCreationDTO
import aguDataSystem.server.domain.agu.AGUDomain
import aguDataSystem.server.domain.agu.AddProviderResult
import aguDataSystem.server.domain.provider.GasProviderInput
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.domain.provider.TemperatureProviderInput
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.errors.agu.AGUCreationError
import aguDataSystem.server.service.errors.agu.GetAGUError
import aguDataSystem.server.service.errors.agu.GetMeasuresError
import aguDataSystem.utils.failure
import aguDataSystem.utils.getSuccessOrThrow
import aguDataSystem.utils.isFailure
import aguDataSystem.utils.isSuccess
import aguDataSystem.utils.success
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

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
            return deleteSuccessProviders(gasRes, tempRes)
        }
        return try {
            transactionManager.run {
                val dno =
                    it.dnoRepository.getByName(aguBasicInfo.dnoName) ?: return@run failure(AGUCreationError.InvalidDNO)
                it.aguRepository.addAGU(aguBasicInfo, dno.id)
                aguBasicInfo.tanks.forEach { tank ->
                    it.tankRepository.addTank(aguBasicInfo.cui, tank)
                }
                aguBasicInfo.contacts.forEach { contact ->
                    it.contactRepository.addContact(aguBasicInfo.cui, contact)
                }
                it.providerRepository.addProvider(aguBasicInfo.cui, gasRes.getSuccessOrThrow(), ProviderType.GAS)
                it.providerRepository.addProvider(
                    aguBasicInfo.cui,
                    tempRes.getSuccessOrThrow(),
                    ProviderType.TEMPERATURE
                )

                success(creationAGU.cui)
            }
        } catch (e: Exception) {
            deleteSuccessProviders(gasRes, tempRes) //TODO: Add a log here that will log in case the deletion is not successful
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
            val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetAGUError.AGUNotFound)

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
            val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetMeasuresError.AGUNotFound)
            val provider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.TEMPERATURE)
                ?: return@run failure(GetMeasuresError.ProviderNotFound)

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
            val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetMeasuresError.AGUNotFound)
            val provider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
                ?: return@run failure(GetMeasuresError.ProviderNotFound)

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
            val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetMeasuresError.AGUNotFound)
            val provider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
                ?: return@run failure(GetMeasuresError.ProviderNotFound)

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
            val agu = it.aguRepository.getAGUByCUI(cui) ?: return@run failure(GetMeasuresError.AGUNotFound)
            val provider = it.providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.GAS)
                ?: return@run failure(GetMeasuresError.ProviderNotFound)

            val levels = it.gasRepository.getPredictionGasMeasures(provider.id, days, time)

            success(levels)
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

    /**
     * Delete the providers that were successfully added
     *
     * @param gasRes the result of the gas provider
     * @param tempRes the result of the temperature provider
     */
    private fun deleteSuccessProviders(gasRes: AddProviderResult, tempRes: AddProviderResult): AGUCreationResult {
        if (gasRes.isSuccess()) {
            aguDomain.deleteProviderRequest(gasRes.getSuccessOrThrow())
        }
        if (tempRes.isSuccess()) {
            aguDomain.deleteProviderRequest(tempRes.getSuccessOrThrow())
        }

        return failure(AGUCreationError.ProviderError)
    }
}