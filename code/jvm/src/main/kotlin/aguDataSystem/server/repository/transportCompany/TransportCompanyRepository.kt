package aguDataSystem.server.repository.transportCompany

import aguDataSystem.server.domain.company.TransportCompany

/**
 * A repository for the transport companies
 */
interface TransportCompanyRepository {

    /**
     * Get all transport companies
     *
     * @return List of Transport Companies
     */
    fun getTransportCompanies(): List<TransportCompany>

    /**
     * Get transport companies by AGU CUI
     *
     * @param cui CUI of AGU
     * @return List of Transport Companies
     */
    fun getTransportCompaniesByAGU(cui: String): List<TransportCompany>

    /**
     * Get a transport company by ID
     *
     * @param id ID of a transport company
     * @return Transport Company
     */
    fun getTransportCompanyById(id: Int): TransportCompany?

    /**
     * Get a transport company by name
     *
     * @param name Name of a transport company
     * @return Transport Company
     */
    fun getTransportCompanyByName(name: String): TransportCompany?

    /**
     * Add a transport company
     *
     * @param name Name of a transport company
     * @return ID of the added transport company
     */
    fun addTransportCompany(name: String): Int


    /**
     * Add a transport company to an AGU
     *
     * @param aguCui CUI of an AGU
     * @param transportCompanyId ID of a transport company
     */
    fun addTransportCompanyToAGU(aguCui: String, transportCompanyId: Int)

    /**
     * Delete a transport company by ID
     *
     * @param id ID of a transport company
     */
    fun deleteTransportCompany(id: Int)

    /**
     * Check if ID stores a transport company
     *
     * @param id ID of a transport company
     * @return True if a transport company is stored, false otherwise
     */
    fun isTransportCompanyStoredById(id: Int): Boolean

    /**
     * Check if name stores a transport company
     *
     * @param name Name of a transport company
     * @return True if a transport company is stored, false otherwise
     */
    fun isTransportCompanyStoredByName(name: String): Boolean
}