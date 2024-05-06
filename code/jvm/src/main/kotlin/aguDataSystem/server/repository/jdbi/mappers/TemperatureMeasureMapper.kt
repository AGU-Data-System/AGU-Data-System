package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.measure.TemperatureMeasure
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to a [TemperatureMeasure]
 * @see RowMapper
 * @see TemperatureMeasure
 */
class TemperatureMeasureMapper: RowMapper<TemperatureMeasure> {

	/**
	 * Maps the row of the result set to a [TemperatureMeasure]
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [TemperatureMeasure] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): TemperatureMeasure {
		var min = Int.MIN_VALUE
		var max = Int.MIN_VALUE
		do {
			when (rs.getString("tag")) {
				TemperatureMeasure::max.name -> max = rs.getInt("data")
				TemperatureMeasure::min.name -> min = rs.getInt("data")
			}
		} while (rs.next() && min == Int.MIN_VALUE && max == Int.MIN_VALUE)

		return TemperatureMeasure(
			timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
			predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
			min = min,
			max = max
		)
	}
}