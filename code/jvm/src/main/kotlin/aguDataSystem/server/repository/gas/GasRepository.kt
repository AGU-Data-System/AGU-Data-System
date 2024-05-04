package aguDataSystem.server.repository.gas

import aguDataSystem.server.domain.measure.GasMeasure
import java.time.LocalDate
import java.time.LocalTime

/**
 * A repository for the gas measures
 */
interface GasRepository {

    /**
     * Gets the gas measures of a provider for a set amount of days
     *
     * @param providerId the id of the provider
     * @param days the amount of days to get the measures from
     * @param time the time to get the measures for
     * @return a list of gas measures
     */
    fun getGasMeasures(providerId: Int, days: Int, time: LocalTime) : List<GasMeasure>

    /**
     * Gets the gas measures of a provider for a specific day
     *
     * @param providerId the id of the provider
     * @param day the day to get the measures from
     * @return a list of gas measures
     */
    fun getGasMeasures(providerId: Int, day: LocalDate): List<GasMeasure>

    /**
     * Gets the gas measures of a provider for a set amount of days
     *
     * @param providerId the id of the provider
     * @param days the amount of days to get the measures from
     * @param time the time to get the measures for
     * @return a list of gas measures
     */
    fun getPredictionGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure>
}