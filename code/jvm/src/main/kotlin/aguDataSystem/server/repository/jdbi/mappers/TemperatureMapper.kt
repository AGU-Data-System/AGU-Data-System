package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Temperature
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

class TemperatureMapper : RowMapper<Temperature> {
	/**
	 * Map the current row of the result set.
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): Temperature {
		return Temperature(
			date = rs.getTimestamp("date").toLocalDateTime(),
			min = rs.getDouble("min"),
			max = rs.getDouble("max"),
			fetchTimeStamp = rs.getTimestamp("timestamp").toLocalDateTime(),
			location = Location(rs.getDouble("latitude"), rs.getDouble("longitude"))
		)
	}
}