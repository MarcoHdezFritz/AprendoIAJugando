package cl.triskeledu.aprendoiajugando.models

data class Lugar(
    val id: Long?,
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val altitud: Double?,
    val qrCodigo: String,
    val productoId: Long?,
    val activo: Boolean
)
