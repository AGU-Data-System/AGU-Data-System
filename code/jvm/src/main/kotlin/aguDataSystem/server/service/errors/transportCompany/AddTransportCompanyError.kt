package aguDataSystem.server.service.errors.transportCompany

import aguDataSystem.server.domain.company.TransportCompany

/**
 * Represents the possible errors that can occur when Adding [TransportCompany]
 *
 * @property InvalidName The name is invalid
 * @property TransportCompanyAlreadyExists The TransportCompany already exists
 */
sealed class AddTransportCompanyError {
	data object InvalidName : AddTransportCompanyError()
	data object TransportCompanyAlreadyExists : AddTransportCompanyError()
}