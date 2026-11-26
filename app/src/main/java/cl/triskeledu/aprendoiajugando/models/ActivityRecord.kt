package cl.triskeledu.aprendoiajugando.models

data class ActivityRecord(
    val id: Int,
    val type: String,
    val description: String,
    val points: Int,
    val createdAt: Long
)
