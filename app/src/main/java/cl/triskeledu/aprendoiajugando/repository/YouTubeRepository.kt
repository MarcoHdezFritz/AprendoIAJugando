package cl.triskeledu.aprendoiajugando.repository

import cl.triskeledu.aprendoiajugando.BuildConfig
import cl.triskeledu.aprendoiajugando.config.YouTubeRetrofitInstance
import cl.triskeledu.aprendoiajugando.models.YouTubeSearchItem
import cl.triskeledu.aprendoiajugando.service.YouTubeApi

class YouTubeRepository(
    private val api: YouTubeApi = YouTubeRetrofitInstance.api,
    private val apiKey: String = BuildConfig.YOUTUBE_API_KEY
) {

    suspend fun buscarVideos(consulta: String): Result<List<YouTubeSearchItem>> {
        return try {
            if (consulta.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("Escribe algo para buscar")
                )
            }

            if (apiKey.isBlank()) {
                return Result.failure(
                    IllegalStateException("No se configuró la clave de YouTube")
                )
            }

            val response = api.buscarVideos(
                consulta = consulta,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                Result.success(response.body()?.items ?: emptyList())
            } else {
                Result.failure(
                    Exception("Error de YouTube: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}