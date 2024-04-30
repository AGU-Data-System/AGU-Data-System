package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.Contact
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row from the database to a [Contact]
 */
class ContactMapper : RowMapper<Contact> {
	/**
	 * Maps a row from the database to a [Contact]
	 *
	 * @param rs the result set
	 * @param ctx the statement context
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): Contact {
		return Contact(
			rs.getString("name"),
			rs.getString("phone"),
			rs.getString("type")
		)
	}
}