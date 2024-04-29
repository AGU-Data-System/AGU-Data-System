package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.AGU
import aguDataSystem.server.domain.Contact
import aguDataSystem.server.domain.DNO
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Provider
import aguDataSystem.server.domain.Reading
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.createContact
import aguDataSystem.server.domain.createProvider
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

class AGUMapper : RowMapper<AGU> {
	override fun map(rs: ResultSet, ctx: StatementContext?): AGU {
		return AGU(
			cui = rs.getString("cui"),
			name = rs.getString("name"),
			levels = mapToGasLevels(rs),
			loadVolume = rs.getInt("load_volume"),
			location = mapToLocation(rs),
			dno = DNO(1, "a"), //TODO() mapToDNO(rs),
			isFavorite = rs.getBoolean("is_favorite"),
			notes = rs.getString("notes"),
			training = rs.getString("training"),
			image = rs.getBytes("image"),
			contacts = mapToContact(rs),
			tanks = emptyList(),
			providers = emptyList()
		)
	}

	private fun mapToContact(rs: ResultSet): List<Contact> {
		val contacts = mutableListOf<Contact>()
		while (rs.next()) {
			val type = rs.getString("type")
			contacts.add(
				type.createContact(
					name = rs.getString("name"),
					phone = rs.getString("phone")
				)
			)
		}
		return contacts
	}

	private fun mapToLocation(rs: ResultSet): Location {
		return Location(
			latitude = rs.getDouble("latitude"),
			longitude = rs.getDouble("longitude"),
			name = rs.getString("location_name")
		)
	}

	private fun mapToGasLevels(rs: ResultSet): GasLevels {
		return GasLevels(
			min = rs.getInt("min_level"),
			max = rs.getInt("max_level"),
			critical = rs.getInt("critical_level")
		)
	}

	private fun mapToTank(rs: ResultSet): List<Tank> {
		val tanks = mutableListOf<Tank>()
		while (rs.next()) {
			tanks.add(
				Tank(
					number = rs.getInt("number"),
					levels = mapToGasLevels(rs),
					loadVolume = rs.getInt("load_volume"),
					capacity = rs.getInt("capacity")
				)
			)
		}
		return tanks
	}

	private fun mapToProvider(rs: ResultSet): List<Provider> {
		val providers = mutableListOf<Provider>()
		while (rs.next()) {
			val type = rs.getString("type")
			providers.add(
				type.createProvider(
					id = rs.getInt("provider_id"),
					readings = mapToReadings(rs)
				)
			)
		}
		return providers
	}

	private fun mapToDNO(rs: ResultSet): DNO {
		return DNO(
			id = rs.getInt("dno_id"),
			name = rs.getString("dno_name")
		)
	}

	private fun mapToReadings(rs: ResultSet): List<Reading> {
		val readings = mutableListOf<Reading>()
		while (rs.next()) {
			readings.add(
				Reading(
					timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
					predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
					data = rs.getInt("data")
				)
			)
		}
		return readings
	}
}