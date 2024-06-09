package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.company.TransportCompany
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to a [TransportCompany]
 * @see RowMapper
 * @see TransportCompany
 */
class TransportCompanyMapper : RowMapper<TransportCompany> {

	override fun map(rs: ResultSet, ctx: StatementContext?): TransportCompany {
		return TransportCompany(
			id = rs.getInt("id"),
			name = rs.getString("name")
		)
	}
}