package cl.triskeledu.aprendoiajugando.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.triskeledu.aprendoiajugando.data.PlayerRepository
import cl.triskeledu.aprendoiajugando.database.PlayerDatabaseHelper
import cl.triskeledu.aprendoiajugando.repository.UsuarioRepository
import cl.triskeledu.aprendoiajugando.models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineDispatcher

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authenticatedName: String? = null,
    val authenticatedRole: String? = null,
    val authenticatedAlias: String? = null,
    val registrationSuccess: Boolean = false,
    val profileUpdateSuccess: Boolean = false,
    val updatedName: String? = null
)


class AuthViewModel(
    application: Application,
    private val playerRepository: PlayerRepository = PlayerRepository(application),
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(),
    private val database: PlayerDatabaseHelper = PlayerDatabaseHelper(application),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // REGISTRO DE CLIENTE
    fun register(
        name: String,
        email: String,
        phone: String,
        address: String,
        password: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                val result = usuarioRepository.registrarUsuario(
                    nombre = name,
                    telefono = phone,
                    email = email,
                    password = password
                )

                if (result.isSuccess) {

                    val localResult = withContext(ioDispatcher) {
                        playerRepository.registerPlayer(
                            name,
                            email,
                            phone,
                            address,
                            password
                        )
                    }

                    val localPlayerExists = withContext(ioDispatcher) {
                        database.getPlayerByEmail(email) != null
                    }

                    _uiState.value =
                        if (localResult || localPlayerExists) {
                            AuthUiState(registrationSuccess = true)
                        } else {
                            AuthUiState(
                                errorMessage = "La cuenta se creó en el servidor, pero no se pudo guardar el perfil local."
                            )
                        }

                } else {
                    _uiState.value = AuthUiState(
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "No se pudo registrar el usuario."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    errorMessage = "Error al registrar: ${e.message}"
                )
            }
        }
    }

    // INICIO DE SESIÓN
    fun login(alias: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                val result = usuarioRepository.iniciarSesion(
                    alias.trim(),
                    password
                )

                if (result.isSuccess) {
                    val usuario = result.getOrThrow()

                    if (usuario.tipoPerfil == "CLIENTE") {
                        withContext(ioDispatcher) {
                            val localPlayer =
                                database.getPlayerByEmail(usuario.alias)

                            if (localPlayer == null) {
                                database.registerPlayer(
                                    usuario.nombre,
                                    usuario.alias,
                                    usuario.telefono,
                                    "",
                                    password
                                )
                            }
                        }
                    }

                    _uiState.value = AuthUiState(
                        authenticatedName = usuario.nombre,
                        authenticatedRole = usuario.tipoPerfil,
                        authenticatedAlias = usuario.alias
                    )

                } else {
                    _uiState.value = AuthUiState(
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Alias o contraseña incorrectos."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }

    // ACTUALIZAR NOMBRE DEL PERFIL
    fun updateProfileName(alias: String, newName: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            try {
                // Buscar los usuarios registrados en el backend.
                val listResult = usuarioRepository.listarUsuarios()

                if (listResult.isFailure) {
                    throw Exception(
                        listResult.exceptionOrNull()?.message
                            ?: "No se pudieron consultar los usuarios."
                    )
                }

                // Encontrar al usuario que inició sesión.
                val usuarioActual = listResult.getOrThrow()
                    .firstOrNull {
                        it.alias.equals(alias.trim(), ignoreCase = true)
                    }
                    ?: throw Exception("No se encontró el usuario.")

                val id = usuarioActual.id
                    ?: throw Exception("El usuario no tiene un ID válido.")

                // Preparar los datos conservando el resto del perfil.
                val usuarioActualizado = Usuario(
                    id = id,
                    nombre = newName.trim(),
                    telefono = usuarioActual.telefono,
                    alias = usuarioActual.alias,
                    tipoPerfil = usuarioActual.tipoPerfil
                )

                // Actualizar primero el microservicio.
                val updateResult = usuarioRepository.actualizarUsuario(
                    id,
                    usuarioActualizado
                )

                if (updateResult.isFailure) {
                    throw Exception(
                        updateResult.exceptionOrNull()?.message
                            ?: "No se pudo actualizar el usuario en el servidor."
                    )
                }

                // Solo si el backend respondió correctamente,
                // actualizar el perfil local en SQLite.
                withContext(ioDispatcher) {
                    database.updatePlayerName(
                        alias.trim(),
                        newName.trim()
                    )
                }

                _uiState.value = AuthUiState(
                    profileUpdateSuccess = true,
                    updatedName = newName.trim()
                )

            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    errorMessage = "No se pudo actualizar el perfil: ${e.message}"
                )
            }
        }
    }

    // LIMPIAR ESTADO
    fun clearState() {
        _uiState.value = AuthUiState()
    }
}
