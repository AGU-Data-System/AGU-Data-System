package aguDataSystem.server.repository

import aguDataSystem.server.repository.jdbi.mappers.agu.AGUBasicInfoMapper
import aguDataSystem.server.repository.jdbi.mappers.agu.AGUMapper
import aguDataSystem.server.repository.jdbi.mappers.alerts.AlertsMapper
import aguDataSystem.server.repository.jdbi.mappers.contact.ContactMapper
import aguDataSystem.server.repository.jdbi.mappers.dno.DNOMapper
import aguDataSystem.server.repository.jdbi.mappers.measures.GasMeasureMapper
import aguDataSystem.server.repository.jdbi.mappers.measures.TemperatureMeasureMapper
import aguDataSystem.server.repository.jdbi.mappers.provider.GasProviderMapper
import aguDataSystem.server.repository.jdbi.mappers.provider.ProviderMapper
import aguDataSystem.server.repository.jdbi.mappers.provider.TemperatureProviderMapper
import aguDataSystem.server.repository.jdbi.mappers.tank.TankMapper
import aguDataSystem.server.repository.jdbi.mappers.transportCompany.TransportCompanyMapper
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

/**
 * Configures the JDBI instance with the requirements of the application
 * @return the configured JDBI instance
 */
fun Jdbi.configureWithAppRequirements(): Jdbi {
	installPlugin(KotlinPlugin())
	installPlugin(PostgresPlugin())

	// dno
	registerRowMapper(DNOMapper())

	//agu
	registerRowMapper(AGUMapper())
	registerRowMapper(AGUBasicInfoMapper())
	registerRowMapper(TankMapper())
	registerRowMapper(ContactMapper())

	// provider
	registerRowMapper(ProviderMapper())
	registerRowMapper(TemperatureProviderMapper())
	registerRowMapper(GasProviderMapper())

	// measure
	registerRowMapper(TemperatureMeasureMapper())
	registerRowMapper(GasMeasureMapper())

	//transport company
	registerRowMapper(TransportCompanyMapper())

	//alerts
	registerRowMapper(AlertsMapper())

	return this
}