package aguDataSystem.server.service.dno

import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.service.errors.dno.CreateDNOError
import aguDataSystem.server.service.errors.dno.GetDNOError
import aguDataSystem.utils.Either

/**
 * Represents the possible results of creating a DNO.
 */
typealias CreateDNOResult = Either<CreateDNOError, DNO>

/**
 * Represents the possible results of getting a DNO by name.
 */
typealias GetDNOByNameResult = Either<GetDNOError, DNO>

/**
 * Represents the possible results of getting all DNOs.
 */
typealias GetDNOByIdError = Either<GetDNOError, DNO>