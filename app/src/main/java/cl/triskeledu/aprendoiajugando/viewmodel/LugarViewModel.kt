
package cl.triskeledu.aprendoiajugando.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.triskeledu.aprendoiajugando.models.Lugar
import cl.triskeledu.aprendoiajugando.repository.LugarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LugarUiState(
    val isLoading: Boolean = false,
    val lugares: List<Lugar> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class LugarViewModel(
    private val repository: LugarRepository = LugarRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LugarUiState())
    val uiState: StateFlow<LugarUiState> = _uiState.asStateFlow()

    fun cargarLugares(limpiarMensajes: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = if (limpiarMensajes) null
                else _uiState.value.successMessage
            )

            val result = repository.listarLugares()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    lugares = result.getOrNull().orEmpty()
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudieron cargar los lugares."
                )
            }
        }
    }

    fun crearLugar(lugar: Lugar) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            val result = repository.crearLugar(lugar)

            if (result.isSuccess) {
                cargarLugares(limpiarMensajes = false)
                _uiState.value = _uiState.value.copy(
                    successMessage = "Lugar registrado correctamente."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo registrar el lugar."
                )
            }
        }
    }

    fun actualizarLugar(id: Long, lugar: Lugar) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            val result = repository.actualizarLugar(id, lugar)

            if (result.isSuccess) {
                cargarLugares(limpiarMensajes = false)
                _uiState.value = _uiState.value.copy(
                    successMessage = "Lugar actualizado correctamente."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo actualizar el lugar."
                )
            }
        }
    }

    fun eliminarLugar(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            val result = repository.eliminarLugar(id)

            if (result.isSuccess) {
                cargarLugares(limpiarMensajes = false)
                _uiState.value = _uiState.value.copy(
                    successMessage = "Lugar eliminado correctamente."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo eliminar el lugar."
                )
            }
        }
    }
}
