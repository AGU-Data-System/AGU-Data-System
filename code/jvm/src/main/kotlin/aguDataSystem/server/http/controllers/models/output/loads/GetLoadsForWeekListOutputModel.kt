package aguDataSystem.server.http.controllers.models.output.loads

import aguDataSystem.server.domain.load.ScheduledLoad

/**
 * Output model for the loads for a week.
 *
 * @property startWeekDay The start of the week.
 * @property endWeekDay The end of the week.
 * @property loads The loads.
 * @property size The size of the loads.
 */
data class GetLoadsForWeekListOutputModel(
	val startWeekDay: String,
	val endWeekDay: String,
	val loads: List<GetLoadsForWeekOutputModel>,
	val size: Int
) {
	constructor(startWeekDay: String, endWeekDay: String, loads: List<ScheduledLoad>) : this(
		startWeekDay = startWeekDay,
		endWeekDay = endWeekDay,
		loads = loads.map { GetLoadsForWeekOutputModel(it) },
		size = loads.size
	)
}