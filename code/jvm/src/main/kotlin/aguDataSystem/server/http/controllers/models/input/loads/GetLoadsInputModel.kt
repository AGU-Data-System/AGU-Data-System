package aguDataSystem.server.http.controllers.models.input.loads

/**
 * Input model for getting loads.
 *
 * @property cui The CUI.
 * @property day The day.
 */
data class GetLoadsInputModel(
	val cui: String,
	val day: String
)