package aguDataSystem.server.service.errors.transportCompany

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.company.TransportCompany

/**
 * Represents the possible errors that occur when obtaining [TransportCompany]s of an [AGU]
 * TODO Needs completion
 */
sealed class GetTransportCompaniesOfAGUError {
	data object AGUNotFound : GetTransportCompaniesOfAGUError()
}