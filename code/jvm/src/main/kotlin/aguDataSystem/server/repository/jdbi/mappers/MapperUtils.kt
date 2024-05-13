package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.toContactType
import java.sql.ResultSet

/**
 * Utility class for mapping database results to domain objects.
 */
object MapperUtils {

	/**
	 * Maps the result set to a list of contacts
	 *
	 * @param rs the result set
	 * @return the list of contacts
	 */
	fun mapToContact(rs: ResultSet): List<Contact> {
		if (rs.getString("contact_name") == null) return emptyList()
		val contacts = mutableListOf<Contact>()
		val cui = rs.getString("cui")
		do {
			contacts.add(
				Contact(
					name = rs.getString("contact_name"),
					phone = rs.getString("contact_phone"),
					type = rs.getString("contact_type").toContactType()
				)
			)
		} while (rs.next() && rs.getString("cui") == cui)
		return contacts.reversed()
	}

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
	 * Maps the result set to a list of tanks
	 *
	 * @param rs the result set
	 * @return the list of tanks
	 */
	fun mapToTank(rs: ResultSet): List<Tank> {
		val tanks = mutableListOf<Tank>()
		while (rs.next()) {
			tanks.add(
				Tank(
					number = rs.getInt("number"),
					levels = mapToGasLevels(rs),
					loadVolume = rs.getInt("load_volume"),
					capacity = rs.getInt("capacity"),
					correctionFactor = rs.getDouble("correction_factor")
				)
			)
		}
		return tanks
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
			name = rs.getString("dno_name")
		)
	}
}