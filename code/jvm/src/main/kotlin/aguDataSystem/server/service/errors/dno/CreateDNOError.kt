package aguDataSystem.server.service.errors.dno

/**
 * Error for creating a DNO
 *
 * @property DNOAlreadyExists The DNO already exists
 * @property InvalidName The name is invalid
 */
sealed class CreateDNOError {
	data object DNOAlreadyExists : CreateDNOError()
	data object InvalidName : CreateDNOError()
}