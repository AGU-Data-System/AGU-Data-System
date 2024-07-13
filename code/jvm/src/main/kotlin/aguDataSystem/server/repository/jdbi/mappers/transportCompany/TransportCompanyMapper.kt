package aguDataSystem.server.repository.jdbi.mappers.transportCompany

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

	/**
	 * Maps a row from the database to a [TransportCompany]
	 *
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [TransportCompany] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): TransportCompany {
		return TransportCompany(
			id = rs.getInt("id"),
			name = rs.getString("name")
		)
	}
}