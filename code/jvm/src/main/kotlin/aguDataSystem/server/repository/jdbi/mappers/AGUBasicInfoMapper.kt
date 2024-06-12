package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.agu.AGUBasicInfo
import aguDataSystem.server.domain.company.TransportCompany
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToDNO
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToLocation
import aguDataSystem.server.repository.jdbi.mappers.MapperUtils.mapToTransportCompany
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
		val eic = rs.getString("eic")
		val dno = mapToDNO(rs)
		val location = mapToLocation(rs)

		val transportCompanies = setOf<TransportCompany>()

		do {
			rs.getString("tc_name") ?: break
			transportCompanies.plus(mapToTransportCompany(rs))
		} while (rs.next() && (rs.getString("cui") == cui).also { transportCompanies.plus(mapToTransportCompany(rs)) })

		return AGUBasicInfo(
			cui = cui,
			eic = eic,
			name = name,
			dno = dno,
			location = location,
			transportCompanies = transportCompanies.toMutableList()
		)
	}
}
