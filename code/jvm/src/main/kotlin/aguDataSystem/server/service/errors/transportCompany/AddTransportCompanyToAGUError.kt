package aguDataSystem.server.service.errors.transportCompany

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyToAGUError.AGUNotFound
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyToAGUError.TransportCompanyNotFound

/**
 * Represents the possible errors that can occur when adding [TransportCompany] to an [AGU]
 *
 * @property AGUNotFound The AGU was not found
 * @property TransportCompanyNotFound The TransportCompany was not found
 */
sealed class AddTransportCompanyToAGUError {
	data object AGUNotFound : AddTransportCompanyToAGUError()
	data object TransportCompanyNotFound : AddTransportCompanyToAGUError()
}