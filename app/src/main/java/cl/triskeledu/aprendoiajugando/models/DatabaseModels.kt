
package cl.triskeledu.aprendoiajugando.models

data class Player(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val points: Int
)

data class TouristPoint(
    val id: Int,
    val name: String,
    val description: String,
    val qrCode: String,
    val latitude: Double,
    val longitude: Double,
    val active: Boolean
)

data class EducationalVideo(
    val id: Int,
    val title: String,
    val url: String,
    val description: String,
    val pointId: Int?
)

data class ActivityRecord(
    val id: Int,
    val type: String,
    val description: String,
    val points: Int,
    val createdAt: Long
)
