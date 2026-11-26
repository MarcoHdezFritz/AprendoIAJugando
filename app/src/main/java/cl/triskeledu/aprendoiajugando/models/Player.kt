package cl.triskeledu.aprendoiajugando.models

data class Player(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val points: Int
)
