package aguDataSystem.server.repository.tank

import aguDataSystem.server.domain.Tank
import org.jdbi.v3.core.Handle

class JDBITankRepository(private val handle: Handle) : TankRepository{
    override fun addTank(cui: String, tank: Tank) {
        handle.createUpdate(
            """
                INSERT INTO tank (agu_cui, number, min_level, max_level, critical_level, load_volume, capacity)
                VALUES (:agu_cui, :number, :min_level, :max_level, :critical_level, :load_volume, :capacity)
            """.trimIndent()
        )
            .bind("agu_cui", cui)
            .bind("number", tank.number)
            .bind("min_level", tank.levels.min)
            .bind("max_level", tank.levels.max)
            .bind("critical_level", tank.levels.critical)
            .bind("load_volume", tank.loadVolume)
            .bind("capacity", tank.capacity)
    }
}