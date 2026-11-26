package cl.triskeledu.aprendoiajugando.models

data class EducationalVideo(
    val id: Int,
    val title: String,
    val url: String,
    val description: String,
    val pointId: Int?
)
