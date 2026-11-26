
package cl.triskeledu.aprendoiajugando.viewmodel

import android.app.Application
import cl.triskeledu.aprendoiajugando.models.Usuario
import cl.triskeledu.aprendoiajugando.data.PlayerRepository
import cl.triskeledu.aprendoiajugando.database.PlayerDatabaseHelper
import cl.triskeledu.aprendoiajugando.repository.UsuarioRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val mainDispatcher = UnconfinedTestDispatcher()

    private lateinit var application: Application
    private lateinit var playerRepository: PlayerRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var database: PlayerDatabaseHelper
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)

        application = mockk(relaxed = true)
        playerRepository = mockk()
        usuarioRepository = mockk()
        database = mockk()

        viewModel = AuthViewModel(
            application = application,
            playerRepository = playerRepository,
            usuarioRepository = usuarioRepository,
            database = database,
            ioDispatcher = mainDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun usuario(
        id: Long? = 1L,
        nombre: String = "Jugador",
        alias: String = "jugador@test.com",
        tipo: String = "CLIENTE"
    ) = Usuario(
        id = id,
        nombre = nombre,
        telefono = "912345678",
        alias = alias,
        tipoPerfil = tipo
    )

    // REGISTRO

    @Test
    fun register_success() = runTest(mainDispatcher) {
        coEvery {
            usuarioRepository.registrarUsuario(
                "Jugador", "912345678", "jugador@test.com", "123456"
            )
        } returns Result.success(usuario())

        every {
            playerRepository.registerPlayer(
                "Jugador", "jugador@test.com", "912345678",
                "Santiago", "123456"
            )
        } returns true

        every {
            database.getPlayerByEmail("jugador@test.com")
        } returns null

        viewModel.register(
            "Jugador", "jugador@test.com", "912345678",
            "Santiago", "123456"
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.registrationSuccess)
        assertNull(viewModel.uiState.value.errorMessage)

        verify(exactly = 1) {
            playerRepository.registerPlayer(
                "Jugador", "jugador@test.com", "912345678",
                "Santiago", "123456"
            )
        }

        verify(exactly = 1) {
            database.getPlayerByEmail("jugador@test.com")
        }
    }

    @Test
    fun register_serverFailure() = runTest(mainDispatcher) {
        coEvery {
            usuarioRepository.registrarUsuario(any(), any(), any(), any())
        } returns Result.failure(Exception("Error servidor"))

        viewModel.register(
            "Jugador", "jugador@test.com", "912345678",
            "Santiago", "123456"
        )

        advanceUntilIdle()

        assertEquals(
            "Error servidor",
            viewModel.uiState.value.errorMessage
        )
        assertFalse(viewModel.uiState.value.registrationSuccess)
    }

    @Test
    fun register_localFailure() = runTest(mainDispatcher) {
        coEvery {
            usuarioRepository.registrarUsuario(any(), any(), any(), any())
        } returns Result.success(usuario())

        every {
            playerRepository.registerPlayer(any(), any(), any(), any(), any())
        } returns false

        every {
            database.getPlayerByEmail(any())
        } returns null

        viewModel.register(
            "Jugador", "jugador@test.com", "912345678",
            "Santiago", "123456"
        )

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.registrationSuccess)
        assertTrue(
            viewModel.uiState.value.errorMessage!!.contains("perfil local")
        )
    }

    @Test
    fun register_localPlayerAlreadyExists() = runTest(mainDispatcher) {
        coEvery {
            usuarioRepository.registrarUsuario(any(), any(), any(), any())
        } returns Result.success(usuario())

        every {
            playerRepository.registerPlayer(any(), any(), any(), any(), any())
        } returns false

        every {
            database.getPlayerByEmail(any())
        } returns mockk(relaxed = true)

        viewModel.register(
            "Jugador", "jugador@test.com", "912345678",
            "Santiago", "123456"
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.registrationSuccess)
    }

    // INICIO DE SESIÓN

    @Test
    fun login_cliente_success_existingLocalPlayer() = runTest(mainDispatcher) {
        val user = usuario()

        coEvery {
            usuarioRepository.iniciarSesion("jugador@test.com", "123456")
        } returns Result.success(user)

        every {
            database.getPlayerByEmail("jugador@test.com")
        } returns mockk(relaxed = true)

        viewModel.login("jugador@test.com", "123456")
        advanceUntilIdle()

        assertEquals("Jugador", viewModel.uiState.value.authenticatedName)
        assertEquals("CLIENTE", viewModel.uiState.value.authenticatedRole)
        assertEquals("jugador@test.com", viewModel.uiState.value.authenticatedAlias)

        verify(exactly = 0) {
            database.registerPlayer(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun login_cliente_success_createsLocalPlayer() = runTest(mainDispatcher) {
        val user = usuario()

        coEvery {
            usuarioRepository.iniciarSesion(any(), any())
        } returns Result.success(user)

        every {
            database.getPlayerByEmail(user.alias)
        } returns null

        every {
            database.registerPlayer(
                user.nombre, user.alias, user.telefono, "", "123456"
            )
        } returns true

        viewModel.login(user.alias, "123456")
        advanceUntilIdle()

        assertEquals("Jugador", viewModel.uiState.value.authenticatedName)

        verify(exactly = 1) {
            database.registerPlayer(
                user.nombre, user.alias, user.telefono, "", "123456"
            )
        }
    }

    @Test
    fun login_admin_success_doesNotUseLocalDatabase() = runTest(mainDispatcher) {
        val admin = usuario(tipo = "ADMIN")

        coEvery {
            usuarioRepository.iniciarSesion(any(), any())
        } returns Result.success(admin)

        viewModel.login("jugador@test.com", "123456")
        advanceUntilIdle()

        assertEquals("ADMIN", viewModel.uiState.value.authenticatedRole)

        verify(exactly = 0) {
            database.getPlayerByEmail(any())
        }
    }

    @Test
    fun login_failure() = runTest(mainDispatcher) {
        coEvery {
            usuarioRepository.iniciarSesion(any(), any())
        } returns Result.failure(Exception("Credenciales incorrectas"))

        viewModel.login("jugador@test.com", "incorrecta")
        advanceUntilIdle()

        assertEquals(
            "Credenciales incorrectas",
            viewModel.uiState.value.errorMessage
        )
        assertNull(viewModel.uiState.value.authenticatedName)
    }

    @Test
    fun login_exception() = runTest(mainDispatcher) {
        coEvery {
            usuarioRepository.iniciarSesion(any(), any())
        } throws Exception("Error inesperado")

        viewModel.login("jugador@test.com", "123456")
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value.errorMessage!!.contains("Error inesperado")
        )
    }

    // ACTUALIZACIÓN DE PERFIL

    @Test
    fun updateProfileName_success() = runTest(mainDispatcher) {
        val currentUser = usuario()
        val updatedUser = currentUser.copy(nombre = "Nuevo Nombre")

        coEvery {
            usuarioRepository.listarUsuarios()
        } returns Result.success(listOf(currentUser))

        coEvery {
            usuarioRepository.actualizarUsuario(1L, updatedUser)
        } returns Result.success(updatedUser)

        every {
            database.updatePlayerName("jugador@test.com", "Nuevo Nombre")
        } returns true

        viewModel.updateProfileName(
            "jugador@test.com", " Nuevo Nombre "
        )
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.profileUpdateSuccess)
        assertEquals("Nuevo Nombre", viewModel.uiState.value.updatedName)
    }

    @Test
    fun updateProfileName_userNotFound() = runTest(mainDispatcher) {
        coEvery {
            usuarioRepository.listarUsuarios()
        } returns Result.success(emptyList())

        viewModel.updateProfileName("inexistente@test.com", "Nuevo Nombre")
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value.errorMessage!!.contains("No se encontró")
        )
        assertFalse(viewModel.uiState.value.profileUpdateSuccess)
    }

    @Test
    fun updateProfileName_userWithoutId() = runTest(mainDispatcher) {
        val user = usuario(id = null)

        coEvery {
            usuarioRepository.listarUsuarios()
        } returns Result.success(listOf(user))

        viewModel.updateProfileName(user.alias, "Nuevo Nombre")
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value.errorMessage!!.contains("ID válido")
        )
    }

    @Test
    fun updateProfileName_listFailure() = runTest(mainDispatcher) {
        coEvery {
            usuarioRepository.listarUsuarios()
        } returns Result.failure(Exception("Error al listar"))

        viewModel.updateProfileName("jugador@test.com", "Nuevo Nombre")
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value.errorMessage!!.contains("Error al listar")
        )
    }

    @Test
    fun updateProfileName_updateFailure() = runTest(mainDispatcher) {
        val currentUser = usuario()
        val updatedUser = currentUser.copy(nombre = "Nuevo Nombre")

        coEvery {
            usuarioRepository.listarUsuarios()
        } returns Result.success(listOf(currentUser))

        coEvery {
            usuarioRepository.actualizarUsuario(1L, updatedUser)
        } returns Result.failure(Exception("No se pudo actualizar"))

        viewModel.updateProfileName(currentUser.alias, "Nuevo Nombre")
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value.errorMessage!!.contains("No se pudo actualizar")
        )
    }

    // ESTADO

    @Test
    fun clearState_resetsUiState() = runTest(mainDispatcher) {
        viewModel.clearState()

        assertEquals(AuthUiState(), viewModel.uiState.value)
    }
}
