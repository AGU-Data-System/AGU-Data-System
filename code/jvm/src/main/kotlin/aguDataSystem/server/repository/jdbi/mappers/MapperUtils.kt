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

    /**
     * Maps the result set to a list of contacts
     *
     * @param rs the result set
     * @return the list of contacts
     */
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
                    capacity = rs.getInt("capacity")
                )
            )
        }
        return tanks
    }

    /**
     * Maps the result set to a list of providers
     *
     * @param rs the result set
     * @return the list of providers
     */
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

    /**
     * Maps the result set to a list of readings based on the provider type
     *
     * @param rs the result set
     * @param type the provider type
     * @return the list of readings
     */
    fun mapToReadings(rs: ResultSet, type: ProviderType): List<Reading> {
        return when (type) {
            ProviderType.GAS -> mapGasReadings(rs)
            ProviderType.TEMPERATURE -> mapTemperatureReadings(rs)
        }
    }

    /**
     * Maps the result set to a list of temperature readings
     *
     * @param rs the result set
     * @return the list of temperature readings
     */
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
                TemperatureReading::min.name-> min = rs.getInt("data")
                TemperatureReading::min.name -> max = rs.getInt("data")
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

    /**
     * Maps the result set to a list of gas readings
     *
     * @param rs the result set
     * @return the list of gas readings
     */
    private fun mapGasReadings(rs: ResultSet): List<GasReading> {
        val readings = mutableListOf<GasReading>()
        while (rs.next()) {
            readings.add(
                ProviderType.GAS.buildReading(
                    timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
                    predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
                    values = intArrayOf(rs.getInt("data"))
                ) as GasReading
            )
        }
        return readings
    }
}