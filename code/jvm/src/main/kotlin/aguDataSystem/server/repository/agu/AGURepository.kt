package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.AGU
import aguDataSystem.server.domain.Location

/**
 * Interface for the AGU repository

 */
interface AGURepository{

	/**
	 * Get all AGUs
	 *
	 * @return List of AGUs
	 */
	fun getAGUs(): List<AGU>

	/**
	 * Get AGU by ID
	 *
	 * @param id ID of AGU
	 * @return AGU
	 */
	fun getAGUById(id: Int): AGU

	/**
	 * Get AGU by location
	 *
	 * @param location Location of AGU
	 * @return AGU
	 */
	fun getAguByLocation(location: Location): AGU?

	/**
	 * Add AGU
	 *
	 * @param agu AGU to add
	 * @return AGU
	 */
	fun addAGU(agu: AGU): AGU

	/**
	 * Update AGU
	 *
	 * @param agu AGU to update
	 * @return AGU
	 */
	fun updateAGU(agu: AGU): AGU
}