package cl.triskeledu.aprendoiajugando.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.triskeledu.aprendoiajugando.models.Usuario
import cl.triskeledu.aprendoiajugando.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UsuarioUiState(
    val isLoading: Boolean = false,
    val usuarios: List<Usuario> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    fun cargarUsuarios(limpiarMensajes: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = if (limpiarMensajes) null
                else _uiState.value.successMessage
            )

            val result = repository.listarUsuarios()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    usuarios = result.getOrNull().orEmpty()
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudieron cargar los usuarios."
                )
            }
        }
    }

    fun crearUsuario(usuario: Usuario) {
        viewModelScope.launch {
            val result = repository.crearUsuario(usuario)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "Usuario creado correctamente."
                )
                cargarUsuarios(limpiarMensajes = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo crear el usuario."
                )
            }
        }
    }

    fun actualizarUsuario(id: Long, usuario: Usuario) {
        viewModelScope.launch {
            val result = repository.actualizarUsuario(id, usuario)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "Usuario actualizado correctamente."
                )
                cargarUsuarios(limpiarMensajes = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo actualizar el usuario."
                )
            }
        }
    }

    fun eliminarUsuario(id: Long) {
        viewModelScope.launch {
            val result = repository.eliminarUsuario(id)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "Usuario eliminado correctamente."
                )
                cargarUsuarios(limpiarMensajes = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo eliminar el usuario."
                )
            }
        }
    }
}
