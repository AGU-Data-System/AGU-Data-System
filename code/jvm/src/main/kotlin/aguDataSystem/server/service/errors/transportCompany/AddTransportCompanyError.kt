package aguDataSystem.server.service.errors.transportCompany

import aguDataSystem.server.domain.company.TransportCompany

/**
 * Represents the possible errors that can occur when Adding [TransportCompany]
 * TODO Needs completion
 */
sealed class AddTransportCompanyError {
	data object InvalidName : AddTransportCompanyError()
	data object TransportCompanyAlreadyExists : AddTransportCompanyError()
}