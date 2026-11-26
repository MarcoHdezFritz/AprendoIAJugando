
package cl.triskeledu.aprendoiajugando.viewmodel

import cl.triskeledu.aprendoiajugando.models.Lugar
import cl.triskeledu.aprendoiajugando.repository.LugarRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LugarViewModelTest {

    private lateinit var repository: LugarRepository
    private lateinit var viewModel: LugarViewModel

    private val lugarEjemplo = Lugar(
        id = 1L,
        nombre = "Plaza de Armas",
        descripcion = "Centro histórico de Santiago",
        latitud = -33.4372,
        longitud = -70.6506,
        altitud = null,
        qrCodigo = "QR-PLAZA-001",
        productoId = null,
        activo = true
    )

    @Before
    fun configurar() {
        Dispatchers.setMain(StandardTestDispatcher())
        repository = mockk()
        viewModel = LugarViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // 1. Cargar lugares correctamente
    @Test
    fun cargarLugares_exitoso() = runTest {
        coEvery {
            repository.listarLugares()
        } returns Result.success(listOf(lugarEjemplo))

        viewModel.cargarLugares()
        advanceUntilIdle()

        assertEquals(
            listOf(lugarEjemplo),
            viewModel.uiState.value.lugares
        )
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    // 2. Error al cargar lugares
    @Test
    fun cargarLugares_error() = runTest {
        coEvery {
            repository.listarLugares()
        } returns Result.failure(Exception("Error de conexión"))

        viewModel.cargarLugares()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.lugares.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error de conexión",
            viewModel.uiState.value.errorMessage
        )
    }

    // 3. Error al cargar lugares sin mensaje específico
    @Test
    fun cargarLugares_errorSinMensaje() = runTest {
        coEvery {
            repository.listarLugares()
        } returns Result.failure(Exception())

        viewModel.cargarLugares()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "No se pudieron cargar los lugares.",
            viewModel.uiState.value.errorMessage
        )
    }

    // 4. Crear lugar correctamente
    @Test
    fun crearLugar_exitoso() = runTest {
        coEvery {
            repository.crearLugar(lugarEjemplo)
        } returns Result.success(lugarEjemplo)

        coEvery {
            repository.listarLugares()
        } returns Result.success(listOf(lugarEjemplo))

        viewModel.crearLugar(lugarEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Lugar registrado correctamente.",
            viewModel.uiState.value.successMessage
        )
        assertEquals(
            listOf(lugarEjemplo),
            viewModel.uiState.value.lugares
        )

        coVerify(exactly = 1) {
            repository.crearLugar(lugarEjemplo)
        }
    }

    // 5. Error al crear lugar
    @Test
    fun crearLugar_error() = runTest {
        coEvery {
            repository.crearLugar(lugarEjemplo)
        } returns Result.failure(Exception("Error al crear lugar"))

        viewModel.crearLugar(lugarEjemplo)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error al crear lugar",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.crearLugar(lugarEjemplo)
        }
    }

    // 6. Actualizar lugar correctamente
    @Test
    fun actualizarLugar_exitoso() = runTest {
        coEvery {
            repository.actualizarLugar(1L, lugarEjemplo)
        } returns Result.success(lugarEjemplo)

        coEvery {
            repository.listarLugares()
        } returns Result.success(listOf(lugarEjemplo))

        viewModel.actualizarLugar(1L, lugarEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Lugar actualizado correctamente.",
            viewModel.uiState.value.successMessage
        )

        coVerify(exactly = 1) {
            repository.actualizarLugar(1L, lugarEjemplo)
        }
    }

    // 7. Error al actualizar lugar
    @Test
    fun actualizarLugar_error() = runTest {
        coEvery {
            repository.actualizarLugar(1L, lugarEjemplo)
        } returns Result.failure(Exception("Error al actualizar lugar"))

        viewModel.actualizarLugar(1L, lugarEjemplo)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error al actualizar lugar",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.actualizarLugar(1L, lugarEjemplo)
        }
    }

    // 8. Eliminar lugar correctamente
    @Test
    fun eliminarLugar_exitoso() = runTest {
        coEvery {
            repository.eliminarLugar(1L)
        } returns Result.success(Unit)

        coEvery {
            repository.listarLugares()
        } returns Result.success(emptyList())

        viewModel.eliminarLugar(1L)
        advanceUntilIdle()

        assertEquals(
            "Lugar eliminado correctamente.",
            viewModel.uiState.value.successMessage
        )
        assertTrue(viewModel.uiState.value.lugares.isEmpty())

        coVerify(exactly = 1) {
            repository.eliminarLugar(1L)
        }
    }

    // 9. Error al eliminar lugar
    @Test
    fun eliminarLugar_error() = runTest {
        coEvery {
            repository.eliminarLugar(1L)
        } returns Result.failure(Exception("Error al eliminar lugar"))

        viewModel.eliminarLugar(1L)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error al eliminar lugar",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.eliminarLugar(1L)
        }
    }
}
