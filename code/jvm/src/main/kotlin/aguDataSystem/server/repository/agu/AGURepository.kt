package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.agu.AGUBasicInfo

/**
 * Interface for the AGU repository

 */
interface AGURepository {

	/**
	 * Get all AGUs
	 *
	 * @return List of AGUs
	 */
	fun getAGUs(): List<AGU>

	/**
	 * Get AGU by CUI
	 *
	 * @param cui CUI of AGU
	 * @return AGU
	 */
	fun getAGUByCUI(cui: String): AGU?

	/**
	 * Get AGU by location
	 *
	 * @param name name of the AGU
	 * @return AGU's CUI code
	 */
	fun getCUIByName(name: String): String?

	/**
	 * Add AGU
	 *
	 * @param aguBasicInfo AGU parameters to create the AGU from
	 * @param dnoID DNO ID
	 * @return AGU's CUI code
	 */
	fun addAGU(aguBasicInfo: AGUBasicInfo, dnoID: Int): String

	/**
	 * Update AGU
	 *
	 * @param agu AGU to update
	 * @return AGU
	 */
	fun updateAGU(agu: AGU): AGU

	/**
	 * Checks whether an AGU exists
	 *
	 * @param cui CUI of AGU
	 * @return True if AGU exists, false otherwise
	 */
	fun isAGUStored(cui: String): Boolean
}