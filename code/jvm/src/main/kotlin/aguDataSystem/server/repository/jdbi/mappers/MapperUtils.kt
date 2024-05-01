package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.toContactType
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.domain.provider.toProviderType
import aguDataSystem.server.domain.reading.GasReading
import aguDataSystem.server.domain.reading.Reading
import aguDataSystem.server.domain.reading.TemperatureReading
import java.sql.ResultSet

/**
 * Utility class for mapping database results to domain objects.
 * TODO needs revision
 */
object MapperUtils {

    fun mapToContact(rs: ResultSet): List<Contact> {
        val contacts = mutableListOf<Contact>()
        while (rs.next()) {
            contacts.add(
                Contact(
                    name = rs.getString("name"),
                    phone = rs.getString("phone"),
					type = rs.getString("type").toContactType()
                )
            )
        }
        return contacts
    }

    fun mapToLocation(rs: ResultSet): Location {
        return Location(
            latitude = rs.getDouble("latitude"),
            longitude = rs.getDouble("longitude"),
            name = rs.getString("location_name")
        )
    }

    fun mapToGasLevels(rs: ResultSet): GasLevels {
        return GasLevels(
            min = rs.getInt("min_level"),
            max = rs.getInt("max_level"),
            critical = rs.getInt("critical_level")
        )
    }

    fun mapToTank(rs: ResultSet): List<Tank> {
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

    fun mapToProvider(rs: ResultSet): List<Provider> {
        val providers = mutableListOf<Provider>()
        while (rs.next()) {
            val type = rs.getString("type").toProviderType()
            providers.add(
                type.createProviderWithReadings(
                    id = rs.getInt("provider_id"),
                    readings = mapToReadings(rs, type)
                )
            )
        }
        return providers
    }

    fun mapToDNO(rs: ResultSet): DNO {
        return DNO(
            id = rs.getInt("dno_id"),
            name = rs.getString("dno_name")
        )
    }

    fun mapToReadings(rs: ResultSet, type: ProviderType): List<Reading> {
        return when (type) {
            ProviderType.GAS -> mapGasReadings(rs)
            ProviderType.TEMPERATURE -> mapTemperatureReadings(rs)
        }
    }

    private fun mapTemperatureReadings(rs: ResultSet): List<TemperatureReading> {
        val readings = mutableListOf<TemperatureReading>()
        var min: Int = -1
        var max: Int = -1
        var acc = 0
        while (rs.next()) {
            if (++acc == 2) {
                acc = 0
                continue
            }
            when (rs.getString("tag")) {
                "min" -> min = rs.getInt("data")
                "max" -> max = rs.getInt("data")
            }
            readings.add(
                ProviderType.TEMPERATURE.buildReading(
                    timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
                    predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
                    values = intArrayOf(min, max)
                ) as TemperatureReading
            )
        }
        return readings
    }

    private fun mapGasReadings(rs: ResultSet): List<GasReading> {
        val readings = mutableListOf<GasReading>()
        while (rs.next()) {
            readings.add(
                ProviderType.GAS.buildReading(
                    timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
                    predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
                    values = intArrayOf(
                        rs.getInt("data")
                    )
                ) as GasReading
            )
        }
        return readings
    }
}