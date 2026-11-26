package cl.triskeledu.aprendoiajugando.models

data class TouristPoint(
    val id: Int,
    val name: String,
    val description: String,
    val qrCode: String,
    val latitude: Double,
    val longitude: Double,
    val active: Boolean
)
