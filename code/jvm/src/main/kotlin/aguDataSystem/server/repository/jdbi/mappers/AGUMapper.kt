package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToContact
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToGasLevels
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToLocation
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

class AGUMapper : RowMapper<AGU> {
	override fun map(rs: ResultSet, ctx: StatementContext?): AGU {
		return AGU(
			cui = rs.getString("cui"),
			name = rs.getString("name"),
			levels = mapToGasLevels(rs),
			loadVolume = rs.getInt("load_volume"),
			location = mapToLocation(rs),
			dno = DNO(1, "a"), //TODO() mapToDNO(rs),
			isFavorite = rs.getBoolean("is_favorite"),
			notes = rs.getString("notes"),
			training = rs.getString("training"),
			image = rs.getBytes("image"),
			contacts = mapToContact(rs),
			tanks = emptyList(),
			providers = emptyList()
		)
	}
}