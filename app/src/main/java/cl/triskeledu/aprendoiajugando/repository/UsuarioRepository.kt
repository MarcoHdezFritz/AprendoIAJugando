package cl.triskeledu.aprendoiajugando.repository

import cl.triskeledu.aprendoiajugando.config.UsuarioRetrofitInstance
import cl.triskeledu.aprendoiajugando.models.Usuario
import cl.triskeledu.aprendoiajugando.models.LoginRequest
import cl.triskeledu.aprendoiajugando.models.UsuarioRegistroRequest
import cl.triskeledu.aprendoiajugando.service.UsuarioApi

class UsuarioRepository(
    private val api: UsuarioApi =
        UsuarioRetrofitInstance.retrofit.create(UsuarioApi::class.java)
) {

    // INICIAR SESIÓN
    suspend fun iniciarSesion(
        alias: String,
        password: String
    ): Result<Usuario> {
        return try {
            val response = api.iniciarSesion(
                LoginRequest(alias, password)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Alias o contraseña incorrectos. HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REGISTRAR CLIENTE
    suspend fun registrarUsuario(
        nombre: String,
        telefono: String,
        email: String,
        password: String
    ): Result<Usuario> {
        return try {
            val request = UsuarioRegistroRequest(
                nombre = nombre,
                telefono = telefono,
                alias = email,
                tipoPerfil = "CLIENTE",
                password = password
            )

            val response = api.registrarUsuario(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("No se pudo registrar. HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CRUD ORIGINAL
    suspend fun listarUsuarios(): Result<List<Usuario>> {
        return try {
            val response = api.listarUsuarios()

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarUsuario(id: Long): Result<Usuario> {
        return try {
            val response = api.buscarUsuario(id)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearUsuario(usuario: Usuario): Result<Usuario> {
        return try {
            val response = api.crearUsuario(usuario)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarUsuario(
        id: Long,
        usuario: Usuario
    ): Result<Usuario> {
        return try {
            val response = api.actualizarUsuario(id, usuario)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarUsuario(id: Long): Result<Boolean> {
        return try {
            val response = api.eliminarUsuario(id)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
