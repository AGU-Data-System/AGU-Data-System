package aguDataSystem.server.repository

import aguDataSystem.server.repository.jdbi.mappers.AGUBasicInfoMapper
import aguDataSystem.server.repository.jdbi.mappers.AGUMapper
import aguDataSystem.server.repository.jdbi.mappers.ContactMapper
import aguDataSystem.server.repository.jdbi.mappers.DNOMapper
import aguDataSystem.server.repository.jdbi.mappers.TankMapper
import aguDataSystem.server.repository.jdbi.mappers.measures.GasMeasureMapper
import aguDataSystem.server.repository.jdbi.mappers.measures.TemperatureMeasureMapper
import aguDataSystem.server.repository.jdbi.mappers.provider.GasProviderMapper
import aguDataSystem.server.repository.jdbi.mappers.provider.ProviderMapper
import aguDataSystem.server.repository.jdbi.mappers.provider.TemperatureProviderMapper
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

	return this
}