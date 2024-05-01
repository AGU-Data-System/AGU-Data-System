package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.toProviderType
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToReadings
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row from the database to a [Provider]
 */
class ProviderMapper : RowMapper<Provider> {
	override fun map(rs: ResultSet, ctx: StatementContext?): Provider {
		val type = rs.getString("type").toProviderType()

		return type.createProviderWithReadings(
			id = rs.getInt("id"),
			readings = mapToReadings(rs, type)
		)
	}

}