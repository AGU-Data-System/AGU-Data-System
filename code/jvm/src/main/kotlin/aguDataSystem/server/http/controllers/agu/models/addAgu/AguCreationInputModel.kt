package aguDataSystem.server.http.controllers.agu.models.addAgu

import aguDataSystem.server.domain.AGUCreationDTO
import aguDataSystem.server.domain.ContactDTO
import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.TankDTO

data class AGUCreationInputModel(
    val cui: String,
    val name: String,
    val minLevel: Int,
    val maxLevel: Int,
    val criticalLevel: Int,
    val loadVolume: Double,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val dnoName: String,
    val gasLevelUrl: String,
    val image: ByteArray,
    val tanks: List<TankInputModel>,
    val contacts: List<ContactInputModel>,
    val isFavorite: Boolean = false,
    val notes: String? = null,
)

data class TankInputModel(
    val number: Int,
    val minLevel: Int,
    val maxLevel: Int,
    val criticalLevel: Int,
    val loadVolume: Double,
    val capacity: Int
)

data class ContactInputModel(
    val name: String,
    val phone: String,
    val type: String
)

fun AGUCreationInputModel.toAGUCreationDTO(): AGUCreationDTO {
    return AGUCreationDTO(
        cui = this.cui,
        name = this.name,
        minLevel = this.minLevel,
        maxLevel = this.maxLevel,
        criticalLevel = this.criticalLevel,
        loadVolume = this.loadVolume.toInt(),
        location = Location(this.locationName, this.latitude, this.longitude),
        dnoName = this.dnoName,
        gasLevelUrl = this.gasLevelUrl,
        image = this.image,
        contacts = this.contacts.map { ContactDTO(it.name, it.phone.toInt(), it.type) },
        tanks = this.tanks.map { TankDTO(it.number, it.minLevel, it.maxLevel, it.criticalLevel, it.loadVolume.toInt(), it.capacity) },
        isFavorite = this.isFavorite,
        notes = this.notes,
        training = null
    )
}