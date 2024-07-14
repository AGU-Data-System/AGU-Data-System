package aguDataSystem.server.http.controllers.models.output.loads

import aguDataSystem.server.domain.load.ScheduledLoad

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