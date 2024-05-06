package aguDataSystem.server.repository

import aguDataSystem.server.repository.jdbi.mappers.AGUMapper
import aguDataSystem.server.repository.jdbi.mappers.ContactMapper
import aguDataSystem.server.repository.jdbi.mappers.DNOMapper
import aguDataSystem.server.repository.jdbi.mappers.GasMeasureMapper
import aguDataSystem.server.repository.jdbi.mappers.ProviderMapper
import aguDataSystem.server.repository.jdbi.mappers.TankMapper
import aguDataSystem.server.repository.jdbi.mappers.TemperatureMeasureMapper
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

	registerRowMapper(AGUMapper())
	registerRowMapper(ContactMapper())
	registerRowMapper(DNOMapper())
	registerRowMapper(ProviderMapper())
	registerRowMapper(TankMapper())
	registerRowMapper(TemperatureMeasureMapper())
	registerRowMapper(GasMeasureMapper())

	return this
}