package aguDataSystem.server.service.dno

import aguDataSystem.server.domain.company.DNO
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
	 * Creates a new DNO with the given [name].
	 *
	 * @param name The name of the DNO to create.
	 * @return The result of the operation.
	 */
	fun createDNO(name: String): CreateDNOResult {
		return transactionManager.run {
			if (name.isEmpty())
				return@run failure(CreateDNOError.InvalidName)

			if (it.dnoRepository.isDNOStoredByName(name))
				return@run failure(CreateDNOError.DNOAlreadyExists)

			val dnoId = it.dnoRepository.addDNO(name)
			success(DNO(dnoId, name))
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