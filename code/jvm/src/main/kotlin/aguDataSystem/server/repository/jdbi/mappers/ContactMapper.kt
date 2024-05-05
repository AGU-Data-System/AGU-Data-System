package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.toContactType
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row from the database to a [Contact]
 * @see RowMapper
 * @see Contact
 */
class ContactMapper : RowMapper<Contact> {

	/**
	 * Maps a row from the database to a [Contact]
	 *
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [Contact] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): Contact {
		return Contact(
			name = rs.getString("name"),
			phone = rs.getString("phone"),
			type = rs.getString("type").toContactType()
		)
	}
}
