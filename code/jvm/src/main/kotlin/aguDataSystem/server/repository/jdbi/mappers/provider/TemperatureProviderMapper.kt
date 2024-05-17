package aguDataSystem.server.repository.jdbi.mappers.provider

import aguDataSystem.server.domain.provider.TemperatureProvider
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to a [TemperatureProvider]
 * @see RowMapper
 * @see TemperatureProvider
 */
class TemperatureProviderMapper : RowMapper<TemperatureProvider> {

	/**
	 * Maps the row of the result set to a [TemperatureProvider]
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [TemperatureProvider] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): TemperatureProvider {
		return TemperatureProvider(
			id = rs.getInt("id"),
			measures = emptyList(),
			lastFetch = rs.getTimestamp("last_fetch")?.toLocalDateTime()
		)
	}
}