package cl.triskeledu.aprendoiajugando.models

data class Usuario(
    val id: Long? = null,
    val nombre: String,
    val telefono: String,
    val alias: String,
    val tipoPerfil: String
)
