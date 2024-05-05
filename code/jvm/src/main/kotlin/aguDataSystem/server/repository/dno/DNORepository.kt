package aguDataSystem.server.repository.dno

import aguDataSystem.server.domain.company.DNO

/**
 * A repository for the DNOs
 */
interface DNORepository {

	/**
	 * Adds a DNO to the repository
	 *
	 * @param name the name of the DNO
	 */
	fun addDNO(name: String): Int

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
    fun isDNOStored(name: String): Boolean
}
