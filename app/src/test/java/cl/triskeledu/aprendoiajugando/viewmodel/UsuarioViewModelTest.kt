package cl.triskeledu.aprendoiajugando.viewmodel

import cl.triskeledu.aprendoiajugando.models.Usuario
import cl.triskeledu.aprendoiajugando.repository.UsuarioRepository
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest {

    private lateinit var repository: UsuarioRepository
    private lateinit var viewModel: UsuarioViewModel

    private val usuarioEjemplo = Usuario(
        id = 1L,
        nombre = "Juan Pérez",
        telefono = "912345678",
        alias = "juan@email.com",
        tipoPerfil = "CLIENTE"
    )

    @Before
    fun configurar() {
        Dispatchers.setMain(StandardTestDispatcher())
        repository = mockk()
        viewModel = UsuarioViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun cargarUsuarios_exitoso() = runTest {
        coEvery {
            repository.listarUsuarios()
        } returns Result.success(listOf(usuarioEjemplo))

        viewModel.cargarUsuarios()
        advanceUntilIdle()

        assertEquals(
            listOf(usuarioEjemplo),
            viewModel.uiState.value.usuarios
        )
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun cargarUsuarios_error() = runTest {
        coEvery {
            repository.listarUsuarios()
        } returns Result.failure(Exception("Error de conexión"))

        viewModel.cargarUsuarios()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error de conexión",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun crearUsuario_exitoso() = runTest {
        coEvery {
            repository.crearUsuario(usuarioEjemplo)
        } returns Result.success(usuarioEjemplo)

        coEvery {
            repository.listarUsuarios()
        } returns Result.success(listOf(usuarioEjemplo))

        viewModel.crearUsuario(usuarioEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Usuario creado correctamente.",
            viewModel.uiState.value.successMessage
        )
        assertEquals(
            listOf(usuarioEjemplo),
            viewModel.uiState.value.usuarios
        )
        assertFalse(viewModel.uiState.value.isLoading)

        coVerify(exactly = 1) {
            repository.crearUsuario(usuarioEjemplo)
        }
    }

    @Test
    fun crearUsuario_error() = runTest {
        coEvery {
            repository.crearUsuario(usuarioEjemplo)
        } returns Result.failure(Exception("Error al crear"))

        viewModel.crearUsuario(usuarioEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Error al crear",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.crearUsuario(usuarioEjemplo)
        }
    }

    @Test
    fun actualizarUsuario_exitoso() = runTest {
        coEvery {
            repository.actualizarUsuario(1L, usuarioEjemplo)
        } returns Result.success(usuarioEjemplo)

        coEvery {
            repository.listarUsuarios()
        } returns Result.success(listOf(usuarioEjemplo))

        viewModel.actualizarUsuario(1L, usuarioEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Usuario actualizado correctamente.",
            viewModel.uiState.value.successMessage
        )
        assertFalse(viewModel.uiState.value.isLoading)

        coVerify(exactly = 1) {
            repository.actualizarUsuario(1L, usuarioEjemplo)
        }
    }

    @Test
    fun actualizarUsuario_error() = runTest {
        coEvery {
            repository.actualizarUsuario(1L, usuarioEjemplo)
        } returns Result.failure(Exception("Error al actualizar"))

        viewModel.actualizarUsuario(1L, usuarioEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Error al actualizar",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.actualizarUsuario(1L, usuarioEjemplo)
        }
    }

    @Test
    fun eliminarUsuario_exitoso() = runTest {
        coEvery {
            repository.eliminarUsuario(1L)
        } returns Result.success(true)

        coEvery {
            repository.listarUsuarios()
        } returns Result.success(emptyList())

        viewModel.eliminarUsuario(1L)
        advanceUntilIdle()

        assertEquals(
            "Usuario eliminado correctamente.",
            viewModel.uiState.value.successMessage
        )
        assertEquals(
            emptyList<Usuario>(),
            viewModel.uiState.value.usuarios
        )
        assertFalse(viewModel.uiState.value.isLoading)

        coVerify(exactly = 1) {
            repository.eliminarUsuario(1L)
        }
    }

    @Test
    fun eliminarUsuario_error() = runTest {
        coEvery {
            repository.eliminarUsuario(1L)
        } returns Result.failure(Exception("Error al eliminar"))

        viewModel.eliminarUsuario(1L)
        advanceUntilIdle()

        assertEquals(
            "Error al eliminar",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.eliminarUsuario(1L)
        }
    }
}
