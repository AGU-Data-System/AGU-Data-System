package aguDataSystem.server.repository.agu

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.agu.AGUCreationInfo
import aguDataSystem.server.domain.gasLevels.GasLevels

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
	 * Update AGU favourite state
	 *
	 * @param cui CUI of AGU
	 * @param isFavorite New favourite state
	 */
	fun updateFavouriteState(cui: String, isFavorite: Boolean)

	/**
	 * Update AGU active state
	 *
	 * @param cui CUI of AGU
	 * @param isActive New active state
	 */
	fun updateActiveState(cui: String, isActive: Boolean)

	/**
	 * Checks whether an AGU exists
	 *
	 * @param cui CUI of AGU
	 * @return True if AGU exists, false otherwise
	 */
	fun isAGUStored(cui: String): Boolean

	/**
	 * Update gas levels of an AGU
	 *
	 * @param cui CUI of AGU
	 * @param levels New gas levels
	 */
	fun updateGasLevels(cui: String, levels: GasLevels)

	/**
	 * Update notes of an AGU
	 *
	 * @param cui CUI of AGU
	 * @param notes New notes
	 */
	fun updateNotes(cui: String, notes: String?)

	/**
	 * Update the training model of an AGU
	 *
	 * @param cui CUI of AGU
	 * @param model New training model
	 */
	fun updateTrainingModel(cui: String, model: String?)
}