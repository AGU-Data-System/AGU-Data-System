package aguDataSystem.server.http.controllers.models.output.loads

import aguDataSystem.server.domain.load.ScheduledLoad

/**
 * Output model for the loads for a week.
 *
 * @property aguCui The AGU CUI.
 * @property date The date.
 * @property timeOfDay The time of day.
 * @property amount The amount.
 * @property isManual If the load is manual.
 * @property isConfirmed If the load is confirmed.
 */
data class GetLoadsForWeekOutputModel(
	val loadId: Int,
	val aguCui: String,
	val date: String,
	val timeOfDay: String,
	val amount: String,
	val isManual: String,
	val isConfirmed: String
) {
	constructor(load: ScheduledLoad) : this(
		loadId = load.id,
		aguCui = load.aguCui,
		date = load.date.toString(),
		timeOfDay = load.timeOfDay.toString(),
		amount = load.amount.toString(),
		isManual = load.isManual.toString(),
		isConfirmed = load.isConfirmed.toString()
	)
}