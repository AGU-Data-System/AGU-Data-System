package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.company.TransportCompany
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
	 * @return the [AGUBasicInfo] from the result set
	 */
	override fun map(rs: ResultSet, ctx: StatementContext?): AGUBasicInfo {
		val cui = rs.getString("cui")
		val name = rs.getString("name")
		val dno = DNO(
			id = rs.getInt("dno_id"),
			name = rs.getString("dno_name"),
			region = rs.getString("region")
		)
		val location = Location(
			latitude = rs.getDouble("latitude"),
			longitude = rs.getDouble("longitude"),
			name = rs.getString("location_name")
		)
		// TODO move this to mapper utils
		val transportCompanies = mutableListOf<TransportCompany>()

		do {
			val tcId = rs.getInt("tc_id")
			val tcName = rs.getString("tc_name") ?: break
			transportCompanies.add(TransportCompany(id = tcId, name = tcName))
		} while (rs.next() && (rs.getString("cui") == cui))

		return AGUBasicInfo(
			cui = cui,
			name = name,
			dno = dno,
			location = location,
			transportCompanies = transportCompanies
		)
	}
}
