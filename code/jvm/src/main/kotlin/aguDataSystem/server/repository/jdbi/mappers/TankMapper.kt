package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.tank.Tank
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToGasLevels
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps a row from the database to a [Tank]
 * @see RowMapper
 * @see Tank
 */
class TankMapper : RowMapper<Tank> {

	/**
	 * Maps a row from the database to a [Tank]
	 *
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [Tank] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): Tank {
		return Tank(
			number = rs.getInt("number"),
			levels = mapToGasLevels(rs),
			capacity = rs.getInt("capacity"),
			correctionFactor = rs.getDouble("correction_factor"),
		)
	}
}
