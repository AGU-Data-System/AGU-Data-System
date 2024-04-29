package aguDataSystem.server.domain

data class AGUCreationDTO(
    val cui: String,
    val name: String,
    val minLevel: Int,
    val maxLevel: Int,
    val criticalLevel: Int,
    val loadVolume: Int,
    val location: Location,
    val dnoName: String,
    val gasLevelUrl: String,
    val image: ByteArray, //TODO: change later to an Image object
    val contacts: List<ContactDTO>,
    val tanks : List<TankDTO>,
    val isFavorite: Boolean = false,
    val notes: String?,
    val training: String?,
)