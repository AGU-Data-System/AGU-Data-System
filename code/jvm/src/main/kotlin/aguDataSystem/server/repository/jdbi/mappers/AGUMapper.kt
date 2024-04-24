package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.AGU
import aguDataSystem.server.domain.Contact
import aguDataSystem.server.domain.toContactType
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

class AGUMapper :RowMapper<AGU> {
	override fun map(rs: ResultSet, ctx: StatementContext?): AGU {
		return AGU(
			cui = rs.getString("cui"),
			name = rs.getString("name"),
			isFavorite = rs.getBoolean("is_favorite"),
			minLevel = rs.getInt("min_level"),
			maxLevel = rs.getInt("max_level"),
			criticalLevel = rs.getInt("critical_level"),
			capacity = rs.getDouble("capacity"),
			latitude = rs.getDouble("latitude"),
			longitude = rs.getDouble("longitude"),
			locationName = rs.getString("location_name"),
			dnoId = rs.getInt("dno_id"),
			notes = rs.getString("notes"),
			training = rs.getString("training"),
			image = rs.getBytes("image"),
			contacts = mapToContact(rs)
		)
	}

	private fun mapToContact(rs: ResultSet): List<Contact> {
		val contacts = mutableListOf<Contact>()
		while (rs.next()) {
			contacts.add(
				Contact(
					name = rs.getString("name"),
					phone = rs.getString("phone"),
					type = rs.getString("type").toContactType(),
				)
			)
		}
		return contacts
	}
}