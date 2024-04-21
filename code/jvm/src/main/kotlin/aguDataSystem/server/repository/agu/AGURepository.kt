package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.AGU

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
	 * @param locationName name of the location of the AGU
	 * @return AGU
	 */
	fun getAguByLocation(locationName: String): AGU?

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