package cl.triskeledu.aprendoiajugando.repository

import cl.triskeledu.aprendoiajugando.models.*
import cl.triskeledu.aprendoiajugando.service.UsuarioApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class UsuarioRepositoryTest {

    private lateinit var api: UsuarioApi
    private lateinit var repository: UsuarioRepository

    private val usuario = Usuario(
        id = 1L,
        nombre = "Marco",
        telefono = "123456789",
        alias = "marco@test.com",
        tipoPerfil = "CLIENTE"
    )

    @Before
    fun setUp() {
        api = mockk()
        repository = UsuarioRepository(api)
    }

    @Test
    fun iniciarSesion_exitoso() = runTest {
        coEvery { api.iniciarSesion(any()) } returns Response.success(usuario)

        val result = repository.iniciarSesion("marco", "1234")

        assertTrue(result.isSuccess)
        assertEquals(usuario, result.getOrNull())
    }

    @Test
    fun iniciarSesion_errorHttp() = runTest {
        coEvery { api.iniciarSesion(any()) } returns Response.error(
            401,
            okhttp3.ResponseBody.create(null, "")
        )

        val result = repository.iniciarSesion("marco", "1234")

        assertTrue(result.isFailure)
    }

    @Test
    fun iniciarSesion_excepcion() = runTest {
        coEvery { api.iniciarSesion(any()) } throws RuntimeException("Error")

        val result = repository.iniciarSesion("marco", "1234")

        assertTrue(result.isFailure)
    }

    @Test
    fun registrarUsuario_exitoso() = runTest {
        coEvery { api.registrarUsuario(any()) } returns Response.success(usuario)

        val result = repository.registrarUsuario(
            "Marco", "123456789", "marco@test.com", "1234"
        )

        assertTrue(result.isSuccess)
        assertEquals(usuario, result.getOrNull())
    }

    @Test
    fun registrarUsuario_errorHttp() = runTest {
        coEvery { api.registrarUsuario(any()) } returns Response.error(
            400,
            okhttp3.ResponseBody.create(null, "")
        )

        val result = repository.registrarUsuario(
            "Marco", "123456789", "marco@test.com", "1234"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun listarUsuarios_exitoso() = runTest {
        coEvery { api.listarUsuarios() } returns Response.success(listOf(usuario))

        val result = repository.listarUsuarios()

        assertTrue(result.isSuccess)
        assertEquals(listOf(usuario), result.getOrNull())
    }

    @Test
    fun listarUsuarios_cuerpoNulo() = runTest {
        coEvery { api.listarUsuarios() } returns Response.success(null)

        val result = repository.listarUsuarios()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull().isNullOrEmpty())
    }

    @Test
    fun listarUsuarios_errorHttp() = runTest {
        coEvery { api.listarUsuarios() } returns Response.error(
            500,
            okhttp3.ResponseBody.create(null, "")
        )

        assertTrue(repository.listarUsuarios().isFailure)
    }

    @Test
    fun listarUsuarios_excepcion() = runTest {
        coEvery { api.listarUsuarios() } throws RuntimeException("Error")

        assertTrue(repository.listarUsuarios().isFailure)
    }

    @Test
    fun buscarUsuario_exitoso() = runTest {
        coEvery { api.buscarUsuario(1L) } returns Response.success(usuario)

        val result = repository.buscarUsuario(1L)

        assertTrue(result.isSuccess)
        assertEquals(usuario, result.getOrNull())
    }

    @Test
    fun buscarUsuario_errorHttp() = runTest {
        coEvery { api.buscarUsuario(1L) } returns Response.error(
            404,
            okhttp3.ResponseBody.create(null, "")
        )

        assertTrue(repository.buscarUsuario(1L).isFailure)
    }

    @Test
    fun crearUsuario_exitoso() = runTest {
        coEvery { api.crearUsuario(usuario) } returns Response.success(usuario)

        assertTrue(repository.crearUsuario(usuario).isSuccess)
    }

    @Test
    fun actualizarUsuario_exitoso() = runTest {
        coEvery { api.actualizarUsuario(1L, usuario) } returns Response.success(usuario)

        assertTrue(repository.actualizarUsuario(1L, usuario).isSuccess)
    }

    @Test
    fun eliminarUsuario_exitoso() = runTest {
        coEvery { api.eliminarUsuario(1L) } returns Response.success(null)

        assertTrue(repository.eliminarUsuario(1L).isSuccess)
    }

    @Test
    fun eliminarUsuario_errorHttp() = runTest {
        coEvery { api.eliminarUsuario(1L) } returns Response.error(
            500,
            okhttp3.ResponseBody.create(null, "")
        )

        assertTrue(repository.eliminarUsuario(1L).isFailure)
    }
}
