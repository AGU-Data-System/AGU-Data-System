package aguDataSystem.server.service.transportCompany

import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyError
import aguDataSystem.server.service.errors.transportCompany.AddTransportCompanyToAGUError
import aguDataSystem.server.service.errors.transportCompany.DeleteTransportCompanyFromAGUError
import aguDataSystem.server.service.errors.transportCompany.GetTransportCompaniesOfAGUError
import aguDataSystem.utils.Either

/**
 * Represents the possible errors of getting transport companies of an AGU.
 */
typealias GetTransportCompaniesOfAGUResult = Either<GetTransportCompaniesOfAGUError, List<TransportCompany>>

/**
 * Represents the possible errors of adding a transport company.
 */
typealias AddTransportCompanyResult = Either<AddTransportCompanyError, Int>

/**
 * Represents the possible errors of adding a transport company to an AGU.
 */
typealias AddTransportCompanyToAGUResult = Either<AddTransportCompanyToAGUError, Unit>

/**
 * Represents the possible errors of deleting a transport company from an AGU.
 */
typealias DeleteTransportCompanyFromAGUResult = Either<DeleteTransportCompanyFromAGUError, Unit>