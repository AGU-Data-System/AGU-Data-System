package aguDataSystem.server.repository.gas

import aguDataSystem.server.domain.measure.GasMeasure
import org.jdbi.v3.core.Handle
import java.time.LocalDate
import java.time.LocalTime

/**
 * JDBI implementation of [GasRepository]
 * @see GasRepository
 * @see Handle
 */
class JDBIGasRepository(private val handle: Handle): GasRepository {

    override fun getGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure> {
        TODO("Not yet implemented")
    }

    override fun getGasMeasures(providerId: Int, day: LocalDate): List<GasMeasure> {
        TODO("Not yet implemented")
    }

    override fun getPredictionGasMeasures(providerId: Int, days: Int, time: LocalTime): List<GasMeasure> {
        TODO("Not yet implemented")
    }

}