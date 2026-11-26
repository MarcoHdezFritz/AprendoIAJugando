package cl.triskeledu.aprendoiajugando.models

data class Producto(
    val id: Long? = null,
    val titulo: String,
    val tema: String,
    val descripcion: String,
    val categoria: String,
    val urlVideo: String,
    val activo: Boolean = true
)
