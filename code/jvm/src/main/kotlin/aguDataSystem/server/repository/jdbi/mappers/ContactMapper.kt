package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.ContactType
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
		val type = if (rs.getString("type")
				.uppercase() == ContactType.LOGISTIC.name
		) ContactType.LOGISTIC else ContactType.EMERGENCY
		return type.createContact(
			name = rs.getString("name"),
			phone = rs.getString("phone")
		)
	}
}