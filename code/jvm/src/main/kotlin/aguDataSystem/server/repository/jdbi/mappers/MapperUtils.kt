package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.gasLevels.GasLevels
import java.sql.ResultSet

/**
 * Utility class for mapping database results to domain objects.
 */
object MapperUtils {

	/**
	 * Maps the result set to a location
	 *
	 * @param rs the result set
	 * @return the location
	 */
	fun mapToLocation(rs: ResultSet): Location {
		return Location(
			latitude = rs.getDouble("latitude"),
			longitude = rs.getDouble("longitude"),
			name = rs.getString("location_name")
		)
	}

	/**
	 * Maps the result set to a list of AGUs
	 *
	 * @param rs the result set
	 * @return the list of AGUs
	 */
	fun mapToGasLevels(rs: ResultSet): GasLevels {
		return GasLevels(
			min = rs.getInt("min_level"),
			max = rs.getInt("max_level"),
			critical = rs.getInt("critical_level")
		)
	}

	/**
	 * Maps the result set to a DNO
	 *
	 * @param rs the result set
	 * @return the DNO
	 */
	fun mapToDNO(rs: ResultSet): DNO {
		return DNO(
			id = rs.getInt("dno_id"),
			name = rs.getString("dno_name"),
			region = rs.getString("region")
		)
	}
}