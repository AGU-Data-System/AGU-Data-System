package aguDataSystem.server.repository.jdbi.mappers.provider

import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.domain.provider.TemperatureProvider
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to a [TemperatureProvider]
 * @see RowMapper
 * @see TemperatureProvider
 */
class TemperatureProviderMapper : RowMapper<TemperatureProvider> {

	/**
	 * Maps the row of the result set to a [TemperatureProvider]
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [TemperatureProvider] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): TemperatureProvider {
		val temperatureMeasures = emptyList<TemperatureMeasure>()
		val id = rs.getInt("id")
		val lastFetch = rs.getTimestamp("last_fetch")
		while (rs.next()) {
			if (rs.wasNull()) break
			temperatureMeasures.plus(
				TemperatureMeasure(
					timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
					predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
					min = rs.getInt("min"),
					max = rs.getInt("max")
				)
			)
		}
		return TemperatureProvider(id = id, measures = temperatureMeasures, lastFetch = lastFetch?.toLocalDateTime())
	}
}