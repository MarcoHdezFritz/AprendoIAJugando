package cl.triskeledu.aprendoiajugando.models

data class LoginRequest(
    val alias: String,
    val password: String
)