package cl.triskeledu.aprendoiajugando.repository

import cl.triskeledu.aprendoiajugando.models.*
import cl.triskeledu.aprendoiajugando.service.YouTubeApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class YouTubeRepositoryTest {

    private lateinit var api: YouTubeApi
    private lateinit var repository: YouTubeRepository

    private val item = YouTubeSearchItem(
        id = YouTubeVideoId("video123"),
        snippet = YouTubeVideoSnippet(
            title = "Aprendiendo IA",
            description = "Video educativo",
            channelTitle = "Triskeledu",
            thumbnails = YouTubeThumbnails()
        )
    )

    @Before
    fun setUp() {
        api = mockk()
        repository = YouTubeRepository(api, "API_KEY_TEST")
    }

    @Test
    fun buscarVideos_exitoso() = runTest {
        coEvery {
            api.buscarVideos("inteligencia artificial", "API_KEY_TEST")
        } returns Response.success(YouTubeSearchResponse(items = listOf(item)))

        val result = repository.buscarVideos("inteligencia artificial")

        assertTrue(result.isSuccess)
        assertEquals(listOf(item), result.getOrNull())
    }

    @Test
    fun buscarVideos_consultaVacia() = runTest {
        val result = repository.buscarVideos(" ")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun buscarVideos_apiKeyVacia() = runTest {
        repository = YouTubeRepository(api, "")

        val result = repository.buscarVideos("inteligencia artificial")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun buscarVideos_respuestaSinElementos() = runTest {
        coEvery {
            api.buscarVideos("historia", "API_KEY_TEST")
        } returns Response.success(YouTubeSearchResponse())

        val result = repository.buscarVideos("historia")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull().isNullOrEmpty())
    }

    @Test
    fun buscarVideos_errorHttp() = runTest {
        coEvery {
            api.buscarVideos("historia", "API_KEY_TEST")
        } returns Response.error(
            403, okhttp3.ResponseBody.create(null, "")
        )

        val result = repository.buscarVideos("historia")

        assertTrue(result.isFailure)
    }

    @Test
    fun buscarVideos_excepcion() = runTest {
        coEvery {
            api.buscarVideos(any(), any(), any(), any(), any(), any(), any(), any())
        } throws RuntimeException("Error de conexión")

        val result = repository.buscarVideos("historia")

        assertTrue(result.isFailure)
        assertEquals("Error de conexión", result.exceptionOrNull()?.message)
    }

    @Test
    fun buscarVideos_enviaConsultaYApiKey() = runTest {
        coEvery {
            api.buscarVideos("museos", "API_KEY_TEST")
        } returns Response.success(YouTubeSearchResponse())

        repository.buscarVideos("museos")

        coVerify {
            api.buscarVideos("museos", "API_KEY_TEST")
        }
    }
}
