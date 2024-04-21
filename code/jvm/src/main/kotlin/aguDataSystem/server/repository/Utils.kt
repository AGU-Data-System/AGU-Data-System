package aguDataSystem.server.repository

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

	return this
}