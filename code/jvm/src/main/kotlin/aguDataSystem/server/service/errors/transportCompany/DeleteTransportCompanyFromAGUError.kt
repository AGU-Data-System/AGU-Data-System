package aguDataSystem.server.service.errors.transportCompany

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.service.errors.transportCompany.DeleteTransportCompanyFromAGUError.AGUNotFound
import aguDataSystem.server.service.errors.transportCompany.DeleteTransportCompanyFromAGUError.TransportCompanyNotFound

/**
 * Represents the possible errors that can occur when Deleting [TransportCompany] from an [AGU]
 *
 * @property TransportCompanyNotFound The TransportCompany was not found
 * @property AGUNotFound The AGU was not found
 */
sealed class DeleteTransportCompanyFromAGUError {
	data object TransportCompanyNotFound : DeleteTransportCompanyFromAGUError()
	data object AGUNotFound : DeleteTransportCompanyFromAGUError()
}