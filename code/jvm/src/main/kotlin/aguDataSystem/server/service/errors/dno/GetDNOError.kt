package aguDataSystem.server.service.errors.dno

/**
 * Represents the possible errors that can occur when getting a DNO by name
 * TODO Needs completion
 */
sealed class GetDNOError {
	data object DNONotFound : GetDNOError()
}