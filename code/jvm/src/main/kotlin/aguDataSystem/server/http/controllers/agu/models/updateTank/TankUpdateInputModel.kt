package aguDataSystem.server.http.controllers.agu.models.updateTank

import aguDataSystem.server.domain.tank.TankUpdateDTO

data class TankUpdateInputModel(
    val minLevel: Int,
    val maxLevel: Int,
    val criticalLevel: Int,
    val loadVolume: Double,
    val capacity: Int,
    val correctionFactor: Double
){
    fun toTankUpdateDTO() = TankUpdateDTO(
        minLevel = this.minLevel,
        maxLevel = this.maxLevel,
        criticalLevel = this.criticalLevel,
        loadVolume = this.loadVolume.toInt(),
        capacity = this.capacity,
        correctionFactor = this.correctionFactor
    )
}