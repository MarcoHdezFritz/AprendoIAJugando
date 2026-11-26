
package cl.triskeledu.aprendoiajugando.repository

import cl.triskeledu.aprendoiajugando.config.RetrofitInstance
import cl.triskeledu.aprendoiajugando.models.Lugar
import cl.triskeledu.aprendoiajugando.service.LugarApi

class LugarRepository(
    private val api: LugarApi =
        RetrofitInstance.retrofit.create(LugarApi::class.java)
) {

    suspend fun listarLugaresActivos(): Result<List<Lugar>> {
        return ejecutarLista { api.listarLugaresActivos() }
    }

    suspend fun listarLugares(): Result<List<Lugar>> {
        return ejecutarLista { api.listarLugares() }
    }

    private suspend fun ejecutarLista(
        llamada: suspend () -> retrofit2.Response<List<Lugar>>
    ): Result<List<Lugar>> {
        return try {
            val response = llamada()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearLugar(lugar: Lugar): Result<Lugar> {
        return ejecutarOperacion { api.crearLugar(lugar) }
    }

    suspend fun actualizarLugar(id: Long, lugar: Lugar): Result<Lugar> {
        return ejecutarOperacion { api.actualizarLugar(id, lugar) }
    }

    suspend fun eliminarLugar(id: Long): Result<Unit> {
        return try {
            val response = api.eliminarLugar(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarLugarPorQr(codigo: String): Result<Lugar> {
        return try {
            val response = api.buscarLugarPorQr(codigo.trim())

            if (response.isSuccessful) {
                val lugar = response.body()

                if (lugar != null) {
                    Result.success(lugar)
                } else {
                    Result.failure(Exception("El servidor no devolvió el lugar."))
                }
            } else {
                Result.failure(
                    Exception("No se encontró el lugar. HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun ejecutarOperacion(
        llamada: suspend () -> retrofit2.Response<Lugar>
    ): Result<Lugar> {
        return try {
            val response = llamada()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.success(body)
                else Result.failure(Exception("El servidor no devolvió el lugar."))
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
