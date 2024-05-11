package aguDataSystem.server.repository.jdbi.mappers.measures

import aguDataSystem.server.domain.measure.GasMeasure
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to a [GasMeasure]
 * @see RowMapper
 * @see GasMeasure
 */
class GasMeasureMapper: RowMapper<GasMeasure> {

	/**
	 * Maps the row of the result set to a [GasMeasure]
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [GasMeasure] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): GasMeasure {
		return GasMeasure(
			timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
			predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
			level = rs.getInt("data")
		)
	}
}