package aguDataSystem.server.repository.jdbi.mappers.provider

import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.provider.GasProvider
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to a [GasProvider]
 * @see RowMapper
 * @see GasProvider
 */
class GasProviderMapper : RowMapper<GasProvider> {

	/**
	 * Maps the row of the result set to a [GasProvider]
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [GasProvider] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): GasProvider {
		val gasMeasures = emptyList<GasMeasure>()
		val id = rs.getInt("id")
		val lastFetch = rs.getTimestamp("last_fetch")
		while (rs.next()) {
			gasMeasures.plus(
				GasMeasure(
					timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
					predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
					level = rs.getInt("level")
				)
			)
		}
		return GasProvider(id = id, measures = gasMeasures, lastFetch = lastFetch?.toLocalDateTime())
	}
}