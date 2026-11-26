package cl.triskeledu.aprendoiajugando.models

data class UsuarioRegistroRequest(
    val nombre: String,
    val telefono: String,
    val alias: String,
    val tipoPerfil: String = "CLIENTE",
    val password: String
)