package cl.triskeledu.aprendoiajugando.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.triskeledu.aprendoiajugando.models.YouTubeSearchItem
import cl.triskeledu.aprendoiajugando.repository.YouTubeRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class YouTubeViewModel(
    private val repository: YouTubeRepository = YouTubeRepository()
) : ViewModel() {

    private val _videos = MutableStateFlow<List<YouTubeSearchItem>>(emptyList())
    val videos: StateFlow<List<YouTubeSearchItem>> = _videos.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun buscarVideos(consulta: String) {
        if (consulta.isBlank()) {
            _error.value = "Escribe algo para buscar"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            _videos.value = emptyList()

            val resultado = repository.buscarVideos(consulta)

            resultado.onSuccess {
                _videos.value = it
            }.onFailure {
                _error.value = it.message ?: "No se pudieron cargar los videos"
            }

            _cargando.value = false
        }
    }
}
