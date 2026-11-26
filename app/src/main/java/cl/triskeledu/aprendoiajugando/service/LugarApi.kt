
package cl.triskeledu.aprendoiajugando.service

import cl.triskeledu.aprendoiajugando.models.Lugar
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LugarApi {

    @GET("api/lugares/activos")
    suspend fun listarLugaresActivos(): Response<List<Lugar>>

    @GET("api/lugares")
    suspend fun listarLugares(): Response<List<Lugar>>

    @GET("api/lugares/qr/{codigo}")
    suspend fun buscarLugarPorQr(
        @Path("codigo") codigo: String
    ): Response<Lugar>

    @POST("api/lugares")
    suspend fun crearLugar(
        @Body lugar: Lugar
    ): Response<Lugar>

    @PUT("api/lugares/{id}")
    suspend fun actualizarLugar(
        @Path("id") id: Long,
        @Body lugar: Lugar
    ): Response<Lugar>

    @DELETE("api/lugares/{id}")
    suspend fun eliminarLugar(
        @Path("id") id: Long
    ): Response<Void>
}
