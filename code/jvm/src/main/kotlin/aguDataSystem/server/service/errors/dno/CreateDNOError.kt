package aguDataSystem.server.service.errors.dno

/**
 * Error for creating a DNO
 *
 * @property DNOAlreadyExists The DNO already exists
 * TODO Needs completion
 */
sealed class CreateDNOError {
	data object DNOAlreadyExists : CreateDNOError()
	data object InvalidName : CreateDNOError()
}