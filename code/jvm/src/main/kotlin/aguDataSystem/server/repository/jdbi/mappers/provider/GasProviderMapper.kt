package aguDataSystem.server.repository.jdbi.mappers.provider

import aguDataSystem.server.domain.provider.GasProvider
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to a [GasProvider]
 * @see RowMapper
 * @see GasProvider
 */
class GasProviderMapper : RowMapper<GasProvider> {

	/**
	 * Maps the row of the result set to a [GasProvider]
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [GasProvider] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): GasProvider {
		return GasProvider(
			id = rs.getInt("id"),
			measures = emptyList(),
			lastFetch = rs.getTimestamp("last_fetch")?.toLocalDateTime()
		)
	}
}