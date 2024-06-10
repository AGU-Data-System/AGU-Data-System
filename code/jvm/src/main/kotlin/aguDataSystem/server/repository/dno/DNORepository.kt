package aguDataSystem.server.repository.dno

import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.company.DNOCreationDTO

/**
 * A repository for the DNOs
 */
interface DNORepository {

	/**
	 * Gets all DNOs
	 *
	 * @return the list of DNOs
	 */
	fun getAll(): List<DNO>

	/**
	 * Adds a DNO to the repository
	 *
	 * @param dnoCreation the creation model for a [DNO]
	 */
	fun addDNO(dnoCreation: DNOCreationDTO): DNO

	/**
	 * Deletes a DNO by its id
	 *
	 * @param id the id of the DNO
	 */
	fun deleteDNO(id: Int)

	/**
	 * Gets DNO by its name
	 *
	 * @param name the name of the DNO
	 * @return the DNO with the given name or null if it doesn't exist
	 */
	fun getByName(name: String): DNO?

	/**
	 * Gets DNO by its id
	 *
	 * @param id the id of the DNO
	 * @return the DNO with the given id or null if it doesn't exist
	 */
	fun getById(id: Int): DNO?

	/**
	 * Checks if a DNO with the given name is stored
	 *
	 * @param name the name of the DNO
	 * @return true if the DNO is stored, false otherwise
	 */
	fun isDNOStoredByName(name: String): Boolean
}
