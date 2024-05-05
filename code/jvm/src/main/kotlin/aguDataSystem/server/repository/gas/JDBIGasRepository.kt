package aguDataSystem.server.repository.gas

import aguDataSystem.server.domain.measure.GasMeasure
import org.jdbi.v3.core.Handle
import java.time.LocalDate
import java.time.LocalTime
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [GasRepository]
 * @see GasRepository
 * @see Handle
 */
class JDBIGasRepository(private val handle: Handle): GasRepository {

    /**
     * Gets the gas measures of a provider for a set number of days
     *
     * @param providerId the id of the provider
     * @param days the number of days to get the measures from
     * @param time the time to get the measures for
     * @return a list of gas measures
     */
    override fun getGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure> {
        TODO("Not yet implemented")
    }

    /**
     * Gets the gas measures of a provider for a specific day
     *
     * @param providerId the id of the provider
     * @param day the day to get the measures from
     * @return a list of gas measures
     */
    override fun getGasMeasures(providerId: Int, day: LocalDate): List<GasMeasure> {
        TODO("Not yet implemented")
    }

    /**
     * Gets the gas measures of a provider for a set number of days
     *
     * @param providerId the id of the provider
     * @param days the number of days to get the measures from
     * @param time the time to get the measures for
     * @return a list of gas measures
     */
    override fun getPredictionGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure> {
        TODO("Not yet implemented")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JDBIGasRepository::class.java)
    }
}