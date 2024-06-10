package aguDataSystem.server.service.errors.transportCompany

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.company.TransportCompany

/**
 * Represents the possible errors that can occur when Deleting [TransportCompany] from an [AGU]
 * TODO Needs completion
 */
sealed class DeleteTransportCompanyFromAGUError {
	data object TransportCompanyNotFound : DeleteTransportCompanyFromAGUError()
	data object AGUNotFound : DeleteTransportCompanyFromAGUError()
}