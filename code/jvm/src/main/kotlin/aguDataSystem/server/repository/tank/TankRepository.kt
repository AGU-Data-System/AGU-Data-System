package aguDataSystem.server.repository.tank

import aguDataSystem.server.domain.Tank

interface TankRepository {
    fun addTank(cui: String, tank: Tank)
}
