package aguDataSystem.server.service.transportCompany

import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.domain.company.TransportCompanyCreationDTO
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyError
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyToAGUError
import aguDataSystem.server.service.errors.transportCompany.DeleteTransportCompanyFromAGUError
import aguDataSystem.server.service.errors.transportCompany.GetTransportCompaniesOfAGUError
import aguDataSystem.utils.failure
import aguDataSystem.utils.success
import org.springframework.stereotype.Service

/**
 * Service for transport companies
 */
@Service
class TransportCompanyService(
    private val transactionManager: TransactionManager,
) {

    /**
     * Get transport companies
     */
    fun getTransportCompanies() : List<TransportCompany> {
        return transactionManager.run {
            val transportCompanies = it.transportCompanyRepository.getTransportCompanies()
            transportCompanies
        }
    }

    /**
     * Get transport companies of an AGU
     */
    fun getTransportCompaniesOfAGU(aguCui: String) : GetTransportCompaniesOfAGUResult {

        return transactionManager.run {
            if(!it.aguRepository.isAGUStored(aguCui))
                return@run failure(GetTransportCompaniesOfAGUError.AGUNotFound)

            val transportCompanies = it.transportCompanyRepository.getTransportCompaniesByAGU(aguCui)
            success(transportCompanies)
        }
    }

    /**
     * Add a transport company
     *
     * @param transportCompanyCreationDTO the transport company to add
     * @return the id of the created transport company
     */
    fun addTransportCompany(transportCompanyCreationDTO: TransportCompanyCreationDTO): AddTransportCompanyResult {

        if (transportCompanyCreationDTO.name.isEmpty())
            return failure(AddTransportCompanyError.InvalidName)

        return transactionManager.run {

            if(it.transportCompanyRepository.isTransportCompanyStoredByName(transportCompanyCreationDTO.name))
                return@run failure(AddTransportCompanyError.TransportCompanyAlreadyExists)

            val transportCompanyId = it.transportCompanyRepository.addTransportCompany(transportCompanyCreationDTO.name)
            success(transportCompanyId)
        }
    }

    /**
     * Delete a transport company
     *
     * @param id the id of the transport company to delete
     */
    fun deleteTransportCompany(id: Int) {
        transactionManager.run {
            it.transportCompanyRepository.deleteTransportCompany(id)
        }
    }

    /**
     * Adds a transport company to an AGU
     *
     * @param aguCui the CUI of the AGU to add the transport company to
     * @param transportCompanyId the ID of the transport company to add
     *
     * @return the result of the operation
     */
    fun addTransportCompanyToAGU(aguCui: String, transportCompanyId: Int): AddTransportCompanyToAGUResult {
        return transactionManager.run {
            if(!it.aguRepository.isAGUStored(aguCui))
                return@run failure(AddTransportCompanyToAGUError.AGUNotFound)

            if(!it.transportCompanyRepository.isTransportCompanyStoredById(transportCompanyId))
                return@run failure(AddTransportCompanyToAGUError.TransportCompanyNotFound)

            it.transportCompanyRepository.addTransportCompanyToAGU(aguCui, transportCompanyId)
            success(Unit)
        }
    }

    /**
     * Deletes a transport company from an AGU
     *
     * @param aguCui the CUI of the AGU to remove the transport company from
     * @param transportCompanyId the ID of the transport company to remove
     *
     * @return the result of the operation
     */
    fun deleteTransportCompanyFromAGU(aguCui: String, transportCompanyId: Int): DeleteTransportCompanyFromAGUResult {
        return transactionManager.run {

            if(!it.aguRepository.isAGUStored(aguCui))
                return@run failure(DeleteTransportCompanyFromAGUError.AGUNotFound)

            if(!it.transportCompanyRepository.isTransportCompanyStoredById(transportCompanyId))
                return@run failure(DeleteTransportCompanyFromAGUError.TransportCompanyNotFound)

            it.transportCompanyRepository.deleteTransportCompanyFromAGU(aguCui, transportCompanyId)
            success(Unit)
        }
    }
}