package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.agu.AGU
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToContact
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToDNO
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToGasLevels
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToLocation
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to an [AGU]
 * @see RowMapper
 * @see AGU
 */
class AGUMapper : RowMapper<AGU> {

	/**
	 * Maps the row of the result set to an [AGU]
	 * TODO smelly due to the fact of contacts and next as well tanks and provides
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [AGU] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): AGU {
		return AGU(
			cui = rs.getString("cui"),
			name = rs.getString("name"),
			levels = mapToGasLevels(rs),
			loadVolume = rs.getInt("load_volume"),
			location = mapToLocation(rs),
			dno = mapToDNO(rs),
			isFavorite = rs.getBoolean("is_favorite"),
			notes = rs.getString("notes"),
			training = rs.getString("training"),
			image = rs.getBytes("image") ?: byteArrayOf(),
			contacts = mapToContact(rs),
			tanks = emptyList(),
			providers = emptyList()
		)
	}
}
