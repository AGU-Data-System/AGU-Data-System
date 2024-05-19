package aguDataSystem.server.http.controllers.agu.models.gasLevels

import aguDataSystem.server.domain.gasLevels.GasLevelsDTO

data class GasLevelsInputModel (
    val min: Int,
    val max: Int,
    val critical: Int
) {
    fun toGasLevelsDTO() = GasLevelsDTO(
        min = this.min,
        max = this.max,
        critical = this.critical
    )
}