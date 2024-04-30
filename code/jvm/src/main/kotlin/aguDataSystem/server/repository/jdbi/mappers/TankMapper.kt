package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.Tank
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToGasLevels
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row from the database to a [Tank]
 */
class TankMapper : RowMapper<Tank> {
	override fun map(rs: ResultSet, ctx: StatementContext?): Tank {
		return Tank(
			number = rs.getInt("number"),
			levels = mapToGasLevels(rs),
			loadVolume = rs.getInt("load_volume"),
			capacity = rs.getInt("capacity")
		)
	}
}