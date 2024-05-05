package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.toProviderType
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToMeasures
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row from the database to a [Provider]
 * @see RowMapper
 * @see Provider
 */
class ProviderMapper : RowMapper<Provider> {

	/**
	 * Maps a row from the database to a [Provider]
	 *
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [Provider] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): Provider {
		val type = rs.getString("type").toProviderType()

		return type.createProviderWithReadings(
			id = rs.getInt("id"),
			measures = mapToMeasures(rs, type)
		)
	}
}
