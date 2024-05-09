package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.agu.AGUCreationInfo

/**
 * Interface for the AGU repository

 */
interface AGURepository {

	/**
	 * Get all AGUs
	 *
	 * @return List of AGUs basic info
	 */
	fun getAGUsBasicInfo(): List<AGUBasicInfo>

	/**
	 * Get AGU by CUI
	 *
	 * @param cui CUI of AGU
	 * @return AGU
	 */
	fun getAGUByCUI(cui: String): AGU?

	/**
	 * Get AGU CUI by name
	 *
	 * @param name name of the AGU
	 * @return AGU's CUI code
	 */
	fun getCUIByName(name: String): String?

	/**
	 * Add AGU
	 *
	 * @param aguCreationInfo AGU parameters to create the AGU from
	 * @param dnoID DNO ID
	 * @return AGU's CUI code
	 */
	fun addAGU(aguCreationInfo: AGUCreationInfo, dnoID: Int): String

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