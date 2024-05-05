package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.GasLevels
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.Tank
import aguDataSystem.server.domain.company.DNO
import aguDataSystem.server.domain.contact.Contact
import aguDataSystem.server.domain.contact.toContactType
import aguDataSystem.server.domain.measure.GasMeasure
import aguDataSystem.server.domain.measure.Measure
import aguDataSystem.server.domain.measure.TemperatureMeasure
import aguDataSystem.server.domain.provider.Provider
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.domain.provider.toProviderType
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
                    measures = mapToMeasures(rs, type)
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
     * Maps the result set to a list of measures based on the provider type
     *
     * @param rs the result set
     * @param type the provider type
     * @return the list of measures
     */
    fun mapToMeasures(rs: ResultSet, type: ProviderType): List<Measure> {
        return when (type) {
            ProviderType.GAS -> mapGasMeasures(rs)
            ProviderType.TEMPERATURE -> mapTemperatureMeasures(rs)
        }
    }

    /**
     * Maps the result set to a list of temperature measures
     *
     * @param rs the result set
     * @return the list of temperature measures
     */
    private fun mapTemperatureMeasures(rs: ResultSet): List<TemperatureMeasure> {
        val readings = mutableListOf<TemperatureMeasure>()
        var min: Int = -1
        var max: Int = -1
        var acc = 0
        while (rs.next()) {
            if (++acc == 2) {
                acc = 0
                continue
            }
            when (rs.getString("tag")) {
                TemperatureMeasure::min.name-> min = rs.getInt("data")
                TemperatureMeasure::min.name -> max = rs.getInt("data")
            }
            readings.add(
                ProviderType.TEMPERATURE.buildMeasure(
                    timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
                    predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
                    values = intArrayOf(min, max)
                ) as TemperatureMeasure
            )
        }
        return readings
    }

    /**
     * Maps the result set to a list of gas measures
     *
     * @param rs the result set
     * @return the list of gas measures
     */
    private fun mapGasMeasures(rs: ResultSet): List<GasMeasure> {
        val readings = mutableListOf<GasMeasure>()
        while (rs.next()) {
            readings.add(
                ProviderType.GAS.buildMeasure(
                    timestamp = rs.getTimestamp("timestamp").toLocalDateTime(),
                    predictionFor = rs.getTimestamp("prediction_for").toLocalDateTime(),
                    values = intArrayOf(rs.getInt("data"))
                ) as GasMeasure
            )
        }
        return readings
    }
}