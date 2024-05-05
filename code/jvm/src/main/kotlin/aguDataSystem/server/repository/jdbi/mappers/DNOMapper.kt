package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.company.DNO
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row from the database to a [DNO]
 * @see RowMapper
 * @see DNO
 */
class DNOMapper : RowMapper<DNO> {

	/**
	 * Maps a row from the database to a [DNO]
	 *
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [DNO] from the result set
	 */
    override fun map(rs: ResultSet, ctx: StatementContext?): DNO {
        return DNO(
            rs.getInt("id"),
            rs.getString("name")
        )
    }
}
