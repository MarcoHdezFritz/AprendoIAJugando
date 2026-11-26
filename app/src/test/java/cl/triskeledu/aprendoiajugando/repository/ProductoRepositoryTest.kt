
package cl.triskeledu.aprendoiajugando.repository

import cl.triskeledu.aprendoiajugando.models.Producto
import cl.triskeledu.aprendoiajugando.service.ProductoApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ProductoRepositoryTest {

    private lateinit var api: ProductoApi
    private lateinit var repository: ProductoRepository

    private val producto = Producto(
        id = 1L,
        titulo = "Introducción a IA",
        tema = "Inteligencia Artificial",
        descripcion = "Video educativo",
        categoria = "Educación",
        urlVideo = "https://youtube.com/watch?v=test",
        activo = true
    )

    @Before
    fun setUp() {
        api = mockk()
        repository = ProductoRepository(api)
    }

    // LISTAR PRODUCTOS

    @Test
    fun listarProductos_exitoso() = runTest {
        coEvery { api.listarProductos() } returns
                Response.success(listOf(producto))

        val result = repository.listarProductos()

        assertTrue(result.isSuccess)
        assertEquals(listOf(producto), result.getOrNull())
    }

    @Test
    fun listarProductos_cuerpoNulo() = runTest {
        coEvery { api.listarProductos() } returns
                Response.success(null)

        val result = repository.listarProductos()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull().isNullOrEmpty())
    }

    @Test
    fun listarProductos_errorHttp() = runTest {
        coEvery { api.listarProductos() } returns
                Response.error(
                    500,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.listarProductos().isFailure)
    }

    @Test
    fun listarProductos_excepcion() = runTest {
        coEvery { api.listarProductos() } throws
                RuntimeException("Error de conexión")

        assertTrue(repository.listarProductos().isFailure)
    }

    // LISTAR PRODUCTOS ACTIVOS

    @Test
    fun listarProductosActivos_exitoso() = runTest {
        coEvery { api.listarProductosActivos() } returns
                Response.success(listOf(producto))

        val result = repository.listarProductosActivos()

        assertTrue(result.isSuccess)
        assertEquals(listOf(producto), result.getOrNull())
    }

    @Test
    fun listarProductosActivos_cuerpoNulo() = runTest {
        coEvery { api.listarProductosActivos() } returns
                Response.success(null)

        val result = repository.listarProductosActivos()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull().isNullOrEmpty())
    }

    @Test
    fun listarProductosActivos_errorHttp() = runTest {
        coEvery { api.listarProductosActivos() } returns
                Response.error(
                    500,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.listarProductosActivos().isFailure)
    }

    @Test
    fun listarProductosActivos_excepcion() = runTest {
        coEvery { api.listarProductosActivos() } throws
                RuntimeException("Error de conexión")

        assertTrue(repository.listarProductosActivos().isFailure)
    }

    // BUSCAR PRODUCTO

    @Test
    fun buscarProducto_exitoso() = runTest {
        coEvery { api.buscarProducto(1L) } returns
                Response.success(producto)

        val result = repository.buscarProducto(1L)

        assertTrue(result.isSuccess)
        assertEquals(producto, result.getOrNull())
    }

    @Test
    fun buscarProducto_errorHttp() = runTest {
        coEvery { api.buscarProducto(1L) } returns
                Response.error(
                    404,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.buscarProducto(1L).isFailure)
    }

    @Test
    fun buscarProducto_cuerpoNulo() = runTest {
        coEvery { api.buscarProducto(1L) } returns
                Response.success<Producto>(null)

        assertTrue(repository.buscarProducto(1L).isFailure)
    }

    @Test
    fun buscarProducto_excepcion() = runTest {
        coEvery { api.buscarProducto(1L) } throws
                RuntimeException("Error de conexión")

        assertTrue(repository.buscarProducto(1L).isFailure)
    }

    // CREAR PRODUCTO

    @Test
    fun crearProducto_exitoso() = runTest {
        coEvery { api.crearProducto(producto) } returns
                Response.success(producto)

        val result = repository.crearProducto(producto)

        assertTrue(result.isSuccess)
        assertEquals(producto, result.getOrNull())
    }

    @Test
    fun crearProducto_errorHttp() = runTest {
        coEvery { api.crearProducto(producto) } returns
                Response.error(
                    400,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.crearProducto(producto).isFailure)
    }

    @Test
    fun crearProducto_cuerpoNulo() = runTest {
        coEvery { api.crearProducto(producto) } returns
                Response.success<Producto>(null)

        assertTrue(repository.crearProducto(producto).isFailure)
    }

    @Test
    fun crearProducto_excepcion() = runTest {
        coEvery { api.crearProducto(producto) } throws
                RuntimeException("Error de conexión")

        assertTrue(repository.crearProducto(producto).isFailure)
    }

    // ACTUALIZAR PRODUCTO

    @Test
    fun actualizarProducto_exitoso() = runTest {
        coEvery {
            api.actualizarProducto(1L, producto)
        } returns Response.success(producto)

        val result = repository.actualizarProducto(1L, producto)

        assertTrue(result.isSuccess)
        assertEquals(producto, result.getOrNull())
    }

    @Test
    fun actualizarProducto_errorHttp() = runTest {
        coEvery {
            api.actualizarProducto(1L, producto)
        } returns Response.error(
            500,
            okhttp3.ResponseBody.create(null, "")
        )

        assertTrue(
            repository.actualizarProducto(1L, producto).isFailure
        )
    }

    @Test
    fun actualizarProducto_cuerpoNulo() = runTest {
        coEvery {
            api.actualizarProducto(1L, producto)
        } returns Response.success<Producto>(null)

        assertTrue(
            repository.actualizarProducto(1L, producto).isFailure
        )
    }

    @Test
    fun actualizarProducto_excepcion() = runTest {
        coEvery {
            api.actualizarProducto(1L, producto)
        } throws RuntimeException("Error de conexión")

        assertTrue(
            repository.actualizarProducto(1L, producto).isFailure
        )
    }

    // ELIMINAR PRODUCTO

    @Test
    fun eliminarProducto_exitoso() = runTest {
        coEvery { api.eliminarProducto(1L) } returns
                Response.success(null)

        val result = repository.eliminarProducto(1L)

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun eliminarProducto_errorHttp() = runTest {
        coEvery { api.eliminarProducto(1L) } returns
                Response.error(
                    500,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.eliminarProducto(1L).isFailure)
    }

    @Test
    fun eliminarProducto_excepcion() = runTest {
        coEvery { api.eliminarProducto(1L) } throws
                RuntimeException("Error de conexión")

        assertTrue(repository.eliminarProducto(1L).isFailure)
    }
}
