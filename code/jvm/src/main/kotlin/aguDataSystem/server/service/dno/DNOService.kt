package aguDataSystem.server.service.dno

import aguDataSystem.server.domain.company.DNOCreationDTO
import aguDataSystem.server.repository.TransactionManager
import aguDataSystem.server.service.errors.dno.CreateDNOError
import aguDataSystem.server.service.errors.dno.GetDNOError
import aguDataSystem.utils.failure
import aguDataSystem.utils.success
import org.springframework.stereotype.Service

/**
 * Service for handling DNO operations
 */
@Service
class DNOService(
	private val transactionManager: TransactionManager,
) {

	/**
	 * Creates a new DNO with the given [DNOCreationDTO].
	 *
	 * @param dnoCreation The creation model for the DNO.
	 * @return The result of the operation.
	 */
	fun createDNO(dnoCreation: DNOCreationDTO): CreateDNOResult {
		return transactionManager.run {
			if (dnoCreation.name.isEmpty())
				return@run failure(CreateDNOError.InvalidName)

			if (it.dnoRepository.isDNOStoredByName(dnoCreation.name))
				return@run failure(CreateDNOError.DNOAlreadyExists)

			val dno = it.dnoRepository.addDNO(dnoCreation)
			success(dno)
		}
	}

	/**
	 * Gets a DNO by its [name].
	 *
	 * @param name The name of the DNO to get.
	 * @return The result of the operation.
	 */
	fun getDNOByName(name: String): GetDNOByNameResult {
		return transactionManager.run {
			val dno = it.dnoRepository.getByName(name) ?: return@run failure(GetDNOError.DNONotFound)

			success(dno)
		}
	}

	/**
	 * Get a DNO by its [id].
	 *
	 * @param id The id of the DNO to get.
	 * @return The DNO with the given id or null if it doesn't exist.
	 */
	fun getDNOById(id: Int): GetDNOByIdError {
		return transactionManager.run {
			val dno = it.dnoRepository.getById(id) ?: return@run failure(GetDNOError.DNONotFound)

			success(dno)
		}
	}

	/**
	 * Checks if a DNO with the given [name] is stored.
	 *
	 * @param name The name of the DNO to check.
	 * @return true if the DNO is stored, false otherwise.
	 */
	fun isDNOStoredByName(name: String): Boolean {
		return transactionManager.run {
			it.dnoRepository.isDNOStoredByName(name)
		}
	}
}