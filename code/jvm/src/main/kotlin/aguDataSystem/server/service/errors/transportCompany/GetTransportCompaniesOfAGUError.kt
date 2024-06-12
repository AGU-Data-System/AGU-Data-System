package aguDataSystem.server.service.errors.transportCompany

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.company.TransportCompany

/**
 * Represents the possible errors that occur when obtaining [TransportCompany]s of an [AGU]
 *
 * @property AGUNotFound The AGU was not found
 */
sealed class GetTransportCompaniesOfAGUError {
	data object AGUNotFound : GetTransportCompaniesOfAGUError()
}