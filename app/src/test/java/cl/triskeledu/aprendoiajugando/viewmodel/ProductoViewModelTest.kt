package cl.triskeledu.aprendoiajugando.viewmodel

import cl.triskeledu.aprendoiajugando.models.Producto
import cl.triskeledu.aprendoiajugando.repository.ProductoRepository
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
class ProductoViewModelTest {

    private lateinit var repository: ProductoRepository
    private lateinit var viewModel: ProductoViewModel

    private val productoEjemplo = Producto(
        id = 1L,
        titulo = "Introducción a la Inteligencia Artificial",
        tema = "Inteligencia Artificial",
        descripcion = "Cápsula educativa sobre IA",
        categoria = "Educación",
        urlVideo = "https://www.youtube.com/watch?v=ejemplo",
        activo = true
    )

    @Before
    fun configurar() {
        Dispatchers.setMain(StandardTestDispatcher())
        repository = mockk()
        viewModel = ProductoViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // 1. Cargar productos correctamente
    @Test
    fun cargarProductos_exitoso() = runTest {
        coEvery {
            repository.listarProductos()
        } returns Result.success(listOf(productoEjemplo))

        viewModel.cargarProductos()
        advanceUntilIdle()

        assertEquals(
            listOf(productoEjemplo),
            viewModel.uiState.value.productos
        )
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    // 2. Error al cargar productos
    @Test
    fun cargarProductos_error() = runTest {
        coEvery {
            repository.listarProductos()
        } returns Result.failure(Exception("Error de conexión"))

        viewModel.cargarProductos()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error de conexión",
            viewModel.uiState.value.errorMessage
        )
    }

    // 3. Crear producto correctamente
    @Test
    fun crearProducto_exitoso() = runTest {
        coEvery {
            repository.crearProducto(productoEjemplo)
        } returns Result.success(productoEjemplo)

        coEvery {
            repository.listarProductos()
        } returns Result.success(listOf(productoEjemplo))

        viewModel.crearProducto(productoEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Cápsula creada correctamente",
            viewModel.uiState.value.successMessage
        )
        assertEquals(
            listOf(productoEjemplo),
            viewModel.uiState.value.productos
        )
        assertFalse(viewModel.uiState.value.isLoading)

        coVerify(exactly = 1) {
            repository.crearProducto(productoEjemplo)
        }
    }

    // 4. Error al crear producto
    @Test
    fun crearProducto_error() = runTest {
        coEvery {
            repository.crearProducto(productoEjemplo)
        } returns Result.failure(Exception("Error al crear cápsula"))

        viewModel.crearProducto(productoEjemplo)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error al crear cápsula",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.crearProducto(productoEjemplo)
        }
    }

    // 5. Actualizar producto correctamente
    @Test
    fun actualizarProducto_exitoso() = runTest {
        coEvery {
            repository.actualizarProducto(1L, productoEjemplo)
        } returns Result.success(productoEjemplo)

        coEvery {
            repository.listarProductos()
        } returns Result.success(listOf(productoEjemplo))

        viewModel.actualizarProducto(1L, productoEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Cápsula actualizada correctamente",
            viewModel.uiState.value.successMessage
        )
        assertFalse(viewModel.uiState.value.isLoading)

        coVerify(exactly = 1) {
            repository.actualizarProducto(1L, productoEjemplo)
        }
    }

    // 6. Error al actualizar producto
    @Test
    fun actualizarProducto_error() = runTest {
        coEvery {
            repository.actualizarProducto(1L, productoEjemplo)
        } returns Result.failure(Exception("Error al actualizar cápsula"))

        viewModel.actualizarProducto(1L, productoEjemplo)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error al actualizar cápsula",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.actualizarProducto(1L, productoEjemplo)
        }
    }

    // 7. Eliminar producto correctamente
    @Test
    fun eliminarProducto_exitoso() = runTest {
        coEvery {
            repository.eliminarProducto(1L)
        } returns Result.success(true)

        coEvery {
            repository.listarProductos()
        } returns Result.success(emptyList())

        viewModel.eliminarProducto(1L)
        advanceUntilIdle()

        assertEquals(
            "Cápsula eliminada correctamente",
            viewModel.uiState.value.successMessage
        )
        assertFalse(viewModel.uiState.value.isLoading)

        coVerify(exactly = 1) {
            repository.eliminarProducto(1L)
        }
    }

    // 8. Error al eliminar producto
    @Test
    fun eliminarProducto_error() = runTest {
        coEvery {
            repository.eliminarProducto(1L)
        } returns Result.failure(Exception("Error al eliminar cápsula"))

        viewModel.eliminarProducto(1L)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Error al eliminar cápsula",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.successMessage)

        coVerify(exactly = 1) {
            repository.eliminarProducto(1L)
        }
    }

    // 9. Limpiar mensajes
    @Test
    fun limpiarMensajes_correctamente() = runTest {
        coEvery {
            repository.crearProducto(productoEjemplo)
        } returns Result.failure(Exception("Error de prueba"))

        viewModel.crearProducto(productoEjemplo)
        advanceUntilIdle()

        assertEquals(
            "Error de prueba",
            viewModel.uiState.value.errorMessage
        )

        viewModel.limpiarMensajes()

        assertNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.successMessage)
    }
}
