package aguDataSystem.server.http.controllers.models.output.agu

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
		agusBasicInfo = agus.map { AGUBasicInfoOutputModel(it) },
		size = agus.size
	)
}
