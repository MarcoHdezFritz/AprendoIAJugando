
package cl.triskeledu.aprendoiajugando.repository

import cl.triskeledu.aprendoiajugando.models.Lugar
import cl.triskeledu.aprendoiajugando.service.LugarApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class LugarRepositoryTest {

    private lateinit var api: LugarApi
    private lateinit var repository: LugarRepository

    private val lugar = Lugar(
        id = 1L,
        nombre = "Plaza de Armas",
        descripcion = "Lugar histórico",
        latitud = -33.4372,
        longitud = -70.6506,
        altitud = null,
        qrCodigo = "PLAZA001",
        productoId = 1L,
        activo = true
    )

    @Before
    fun setUp() {
        api = mockk()
        repository = LugarRepository(api)
    }

    // LISTAR LUGARES ACTIVOS

    @Test
    fun listarLugaresActivos_exitoso() = runTest {
        coEvery { api.listarLugaresActivos() } returns
                Response.success(listOf(lugar))

        val result = repository.listarLugaresActivos()

        assertTrue(result.isSuccess)
        assertEquals(listOf(lugar), result.getOrNull())
    }

    @Test
    fun listarLugaresActivos_cuerpoNulo() = runTest {
        coEvery { api.listarLugaresActivos() } returns
                Response.success<List<Lugar>>(null)

        val result = repository.listarLugaresActivos()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull().isNullOrEmpty())
    }

    @Test
    fun listarLugaresActivos_errorHttp() = runTest {
        coEvery { api.listarLugaresActivos() } returns
                Response.error(
                    500,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.listarLugaresActivos().isFailure)
    }

    @Test
    fun listarLugaresActivos_excepcion() = runTest {
        coEvery { api.listarLugaresActivos() } throws
                RuntimeException("Error")

        assertTrue(repository.listarLugaresActivos().isFailure)
    }

    // LISTAR TODOS LOS LUGARES

    @Test
    fun listarLugares_exitoso() = runTest {
        coEvery { api.listarLugares() } returns
                Response.success(listOf(lugar))

        val result = repository.listarLugares()

        assertTrue(result.isSuccess)
        assertEquals(listOf(lugar), result.getOrNull())
    }

    @Test
    fun listarLugares_cuerpoNulo() = runTest {
        coEvery { api.listarLugares() } returns
                Response.success<List<Lugar>>(null)

        val result = repository.listarLugares()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull().isNullOrEmpty())
    }

    @Test
    fun listarLugares_errorHttp() = runTest {
        coEvery { api.listarLugares() } returns
                Response.error(
                    500,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.listarLugares().isFailure)
    }

    @Test
    fun listarLugares_excepcion() = runTest {
        coEvery { api.listarLugares() } throws
                RuntimeException("Error")

        assertTrue(repository.listarLugares().isFailure)
    }

    // BUSCAR LUGAR POR QR

    @Test
    fun buscarLugarPorQr_exitoso() = runTest {
        coEvery {
            api.buscarLugarPorQr("PLAZA001")
        } returns Response.success(lugar)

        val result = repository.buscarLugarPorQr(" PLAZA001 ")

        assertTrue(result.isSuccess)
        assertEquals(lugar, result.getOrNull())

        coVerify {
            api.buscarLugarPorQr("PLAZA001")
        }
    }

    @Test
    fun buscarLugarPorQr_errorHttp() = runTest {
        coEvery {
            api.buscarLugarPorQr("INVALIDO")
        } returns Response.error(
            404,
            okhttp3.ResponseBody.create(null, "")
        )

        assertTrue(
            repository.buscarLugarPorQr("INVALIDO").isFailure
        )
    }

    @Test
    fun buscarLugarPorQr_cuerpoNulo() = runTest {
        coEvery {
            api.buscarLugarPorQr("PLAZA001")
        } returns Response.success<Lugar>(null)

        val result = repository.buscarLugarPorQr("PLAZA001")

        assertTrue(result.isFailure)
        assertEquals(
            "El servidor no devolvió el lugar.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun buscarLugarPorQr_excepcion() = runTest {
        coEvery {
            api.buscarLugarPorQr("PLAZA001")
        } throws RuntimeException("Error")

        assertTrue(
            repository.buscarLugarPorQr("PLAZA001").isFailure
        )
    }

    // CREAR LUGAR

    @Test
    fun crearLugar_exitoso() = runTest {
        coEvery { api.crearLugar(lugar) } returns
                Response.success(lugar)

        val result = repository.crearLugar(lugar)

        assertTrue(result.isSuccess)
        assertEquals(lugar, result.getOrNull())
    }

    @Test
    fun crearLugar_errorHttp() = runTest {
        coEvery { api.crearLugar(lugar) } returns
                Response.error(
                    400,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.crearLugar(lugar).isFailure)
    }

    @Test
    fun crearLugar_cuerpoNulo() = runTest {
        coEvery { api.crearLugar(lugar) } returns
                Response.success<Lugar>(null)

        assertTrue(repository.crearLugar(lugar).isFailure)
    }

    @Test
    fun crearLugar_excepcion() = runTest {
        coEvery { api.crearLugar(lugar) } throws
                RuntimeException("Error")

        assertTrue(repository.crearLugar(lugar).isFailure)
    }

    // ACTUALIZAR LUGAR

    @Test
    fun actualizarLugar_exitoso() = runTest {
        coEvery {
            api.actualizarLugar(1L, lugar)
        } returns Response.success(lugar)

        val result = repository.actualizarLugar(1L, lugar)

        assertTrue(result.isSuccess)
        assertEquals(lugar, result.getOrNull())
    }

    @Test
    fun actualizarLugar_errorHttp() = runTest {
        coEvery {
            api.actualizarLugar(1L, lugar)
        } returns Response.error(
            500,
            okhttp3.ResponseBody.create(null, "")
        )

        assertTrue(
            repository.actualizarLugar(1L, lugar).isFailure
        )
    }

    @Test
    fun actualizarLugar_cuerpoNulo() = runTest {
        coEvery {
            api.actualizarLugar(1L, lugar)
        } returns Response.success<Lugar>(null)

        assertTrue(
            repository.actualizarLugar(1L, lugar).isFailure
        )
    }

    @Test
    fun actualizarLugar_excepcion() = runTest {
        coEvery {
            api.actualizarLugar(1L, lugar)
        } throws RuntimeException("Error")

        assertTrue(
            repository.actualizarLugar(1L, lugar).isFailure
        )
    }

    // ELIMINAR LUGAR

    @Test
    fun eliminarLugar_exitoso() = runTest {
        coEvery { api.eliminarLugar(1L) } returns
                Response.success(null)

        val result = repository.eliminarLugar(1L)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun eliminarLugar_errorHttp() = runTest {
        coEvery { api.eliminarLugar(1L) } returns
                Response.error(
                    500,
                    okhttp3.ResponseBody.create(null, "")
                )

        assertTrue(repository.eliminarLugar(1L).isFailure)
    }

    @Test
    fun eliminarLugar_excepcion() = runTest {
        coEvery { api.eliminarLugar(1L) } throws
                RuntimeException("Error")

        assertTrue(repository.eliminarLugar(1L).isFailure)
    }
}
