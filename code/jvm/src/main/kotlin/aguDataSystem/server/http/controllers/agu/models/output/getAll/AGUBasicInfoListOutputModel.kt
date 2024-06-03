package aguDataSystem.server.http.controllers.agu.models.output.getAll

import aguDataSystem.server.domain.agu.AGUBasicInfo

/**
 * Output model for AGUBasicInfoList
 *
 * @param agusBasicInfo List of AGUBasicInfoOutputModel
 * @param size Size of the list
 */
data class AGUBasicInfoListOutputModel(
	val agusBasicInfo: List<AGUBasicInfoOutputModel>,
	val size: Int
) {
	constructor(agus: List<AGUBasicInfo>) : this(
		agus.map { AGUBasicInfoOutputModel(it) },
		agus.size
	)
}
