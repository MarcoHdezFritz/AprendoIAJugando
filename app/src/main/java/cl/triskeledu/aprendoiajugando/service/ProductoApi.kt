package cl.triskeledu.aprendoiajugando.service

import cl.triskeledu.aprendoiajugando.models.Producto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductoApi {

    @GET("api/productos")
    suspend fun listarProductos(): Response<List<Producto>>

    @GET("api/productos/activos")
    suspend fun listarProductosActivos(): Response<List<Producto>>

    @GET("api/productos/{id}")
    suspend fun buscarProducto(
        @Path("id") id: Long
    ): Response<Producto>

    @POST("api/productos")
    suspend fun crearProducto(
        @Body producto: Producto
    ): Response<Producto>

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: Producto
    ): Response<Producto>

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    ): Response<Void>
}
