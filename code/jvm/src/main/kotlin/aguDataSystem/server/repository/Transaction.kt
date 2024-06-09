package aguDataSystem.server.repository

import aguDataSystem.server.repository.agu.AGURepository
import aguDataSystem.server.repository.contact.ContactRepository
import aguDataSystem.server.repository.dno.DNORepository
import aguDataSystem.server.repository.gas.GasRepository
import aguDataSystem.server.repository.provider.ProviderRepository
import aguDataSystem.server.repository.tank.TankRepository
import aguDataSystem.server.repository.temperature.TemperatureRepository
import aguDataSystem.server.repository.transportCompany.TransportCompanyRepository

/**
 * A transaction for the repositories
 */
interface Transaction {

	// other repository types
	val aguRepository: AGURepository

	val providerRepository: ProviderRepository

	val dnoRepository: DNORepository

	val tankRepository: TankRepository

	val contactRepository: ContactRepository

	val temperatureRepository: TemperatureRepository

	val gasRepository: GasRepository

	val transportCompanyRepository: TransportCompanyRepository

	/**
	 * Rolls back the transaction
	 */
	fun rollback()
}