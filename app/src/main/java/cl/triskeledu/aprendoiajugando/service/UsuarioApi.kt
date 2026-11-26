
package cl.triskeledu.aprendoiajugando.service

import cl.triskeledu.aprendoiajugando.models.LoginRequest
import cl.triskeledu.aprendoiajugando.models.Usuario
import cl.triskeledu.aprendoiajugando.models.UsuarioRegistroRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApi {

    // AUTENTICACIÓN
    @POST("api/usuarios/login")
    suspend fun iniciarSesion(
        @Body request: LoginRequest
    ): Response<Usuario>

    // REGISTRO
    @POST("api/usuarios")
    suspend fun registrarUsuario(
        @Body request: UsuarioRegistroRequest
    ): Response<Usuario>

    // CRUD DE USUARIOS
    @GET("api/usuarios")
    suspend fun listarUsuarios(): Response<List<Usuario>>

    @GET("api/usuarios/{id}")
    suspend fun buscarUsuario(
        @Path("id") id: Long
    ): Response<Usuario>

    @POST("api/usuarios")
    suspend fun crearUsuario(
        @Body usuario: Usuario
    ): Response<Usuario>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: Usuario
    ): Response<Usuario>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(
        @Path("id") id: Long
    ): Response<Void>
}
