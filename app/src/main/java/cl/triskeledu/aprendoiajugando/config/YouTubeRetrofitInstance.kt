package cl.triskeledu.aprendoiajugando.config

import cl.triskeledu.aprendoiajugando.service.YouTubeApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object YouTubeRetrofitInstance {

    private const val BASE_URL = "https://www.googleapis.com/"

    val api: YouTubeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApi::class.java)
    }
}
