package cl.triskeledu.aprendoiajugando.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.triskeledu.aprendoiajugando.models.Producto
import cl.triskeledu.aprendoiajugando.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductoUiState(
    val productos: List<Producto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ProductoViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            repository.listarProductos()
                .onSuccess { productos ->
                    _uiState.value = _uiState.value.copy(
                        productos = productos,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun crearProducto(producto: Producto) {
        ejecutarOperacion("Cápsula creada correctamente") {
            repository.crearProducto(producto)
        }
    }

    fun actualizarProducto(id: Long, producto: Producto) {
        ejecutarOperacion("Cápsula actualizada correctamente") {
            repository.actualizarProducto(id, producto)
        }
    }

    fun eliminarProducto(id: Long) {
        ejecutarOperacion("Cápsula eliminada correctamente") {
            repository.eliminarProducto(id)
        }
    }

    private fun ejecutarOperacion(
        mensaje: String,
        operacion: suspend () -> Result<*>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            operacion()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = mensaje
                    )
                    cargarProductos()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}
