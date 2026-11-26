package cl.triskeledu.aprendoiajugando.service

import cl.triskeledu.aprendoiajugando.models.YouTubeSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {

    @GET("youtube/v3/search")
    suspend fun buscarVideos(
        @Query("q") consulta: String,
        @Query("key") apiKey: String,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("regionCode") regionCode: String = "CL",
        @Query("relevanceLanguage") relevanceLanguage: String = "es",
        @Query("videoEmbeddable") videoEmbeddable: String = "true",
        @Query("maxResults") maxResults: Int = 10
    ): Response<YouTubeSearchResponse>
}
