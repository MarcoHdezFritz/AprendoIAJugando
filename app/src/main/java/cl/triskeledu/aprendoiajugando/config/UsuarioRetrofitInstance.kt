package cl.triskeledu.aprendoiajugando.config

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UsuarioRetrofitInstance {

    private val BASE_URL = ApiBaseUrl.forPort(8081)

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
