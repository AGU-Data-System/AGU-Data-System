package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.agu.AGUCreationInfo
import aguDataSystem.server.domain.company.DNO
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to an [AGUBasicInfo]
 *
 * @see RowMapper
 * @see AGUBasicInfo
 */
class AGUBasicInfoMapper : RowMapper<AGUBasicInfo> {

	/**
	 * Maps the row of the result set to an [AGUBasicInfo]
	 *
	 * @param rs the result set
	 * @param ctx the statement context
	 * @return the [AGUCreationInfo] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): AGUBasicInfo {
		return AGUBasicInfo(
			cui = rs.getString("cui"),
			name = rs.getString("name"),
			dno = DNO(
				id = rs.getInt("dno_id"),
				name = rs.getString("dno_name")
			),
			location = Location(
				latitude = rs.getDouble("latitude"),
				longitude = rs.getDouble("longitude"),
				name = rs.getString("location_name")
			)
		)
	}
}