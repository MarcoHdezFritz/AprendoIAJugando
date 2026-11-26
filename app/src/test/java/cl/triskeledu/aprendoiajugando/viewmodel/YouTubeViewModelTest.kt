package cl.triskeledu.aprendoiajugando.viewmodel

import cl.triskeledu.aprendoiajugando.models.*
import cl.triskeledu.aprendoiajugando.repository.YouTubeRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class YouTubeViewModelTest {

    private val mainDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: YouTubeRepository
    private lateinit var viewModel: YouTubeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)

        repository = mockk()
        viewModel = YouTubeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun video() = YouTubeSearchItem(
        id = YouTubeVideoId(videoId = "abc123"),
        snippet = YouTubeVideoSnippet(
            title = "Aprendiendo IA",
            description = "Video de inteligencia artificial",
            channelTitle = "Triskeledu",
            thumbnails = YouTubeThumbnails(
                medium = YouTubeThumbnail(
                    url = "https://example.com/thumbnail.jpg"
                )
            )
        )
    )

    // BÚSQUEDA DE VIDEOS

    @Test
    fun buscarVideos_success() = runTest(mainDispatcher) {
        val videos = listOf(video())

        coEvery {
            repository.buscarVideos("inteligencia artificial")
        } returns Result.success(videos)

        viewModel.buscarVideos("inteligencia artificial")
        advanceUntilIdle()

        assertEquals(videos, viewModel.videos.value)
        assertFalse(viewModel.cargando.value)
        assertNull(viewModel.error.value)

        coVerify(exactly = 1) {
            repository.buscarVideos("inteligencia artificial")
        }
    }

    @Test
    fun buscarVideos_emptyQuery() = runTest(mainDispatcher) {
        viewModel.buscarVideos("   ")
        advanceUntilIdle()

        assertEquals("Escribe algo para buscar", viewModel.error.value)
        assertTrue(viewModel.videos.value.isEmpty())

        coVerify(exactly = 0) {
            repository.buscarVideos(any())
        }
    }

    @Test
    fun buscarVideos_failure() = runTest(mainDispatcher) {
        coEvery {
            repository.buscarVideos("videos inexistentes")
        } returns Result.failure(Exception("Error de conexión"))

        viewModel.buscarVideos("videos inexistentes")
        advanceUntilIdle()

        assertEquals("Error de conexión", viewModel.error.value)
        assertTrue(viewModel.videos.value.isEmpty())
        assertFalse(viewModel.cargando.value)
    }

    @Test
    fun buscarVideos_failureWithoutMessage() = runTest(mainDispatcher) {
        coEvery {
            repository.buscarVideos("prueba")
        } returns Result.failure(Exception())

        viewModel.buscarVideos("prueba")
        advanceUntilIdle()

        assertEquals(
            "No se pudieron cargar los videos",
            viewModel.error.value
        )
        assertFalse(viewModel.cargando.value)
    }
}
