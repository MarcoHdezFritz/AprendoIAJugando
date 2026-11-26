package cl.triskeledu.aprendoiajugando.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.triskeledu.aprendoiajugando.viewmodel.YouTubeViewModel

@Composable
fun YouTubeScreen(
    onBack: () -> Unit,
    youtubeViewModel: YouTubeViewModel = viewModel()
) {
    val videos by youtubeViewModel.videos.collectAsState()
    val cargando by youtubeViewModel.cargando.collectAsState()
    val error by youtubeViewModel.error.collectAsState()

    var consulta by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Volver")
        }

        Text(
            text = "Explora videos de YouTube",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = consulta,
            onValueChange = { consulta = it },
            label = { Text("Buscar videos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                youtubeViewModel.buscarVideos(consulta)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = consulta.isNotBlank() && !cargando
        ) {
            Text("Buscar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (cargando) {
            CircularProgressIndicator()
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(videos) { video ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val videoId = video.id.videoId

                            if (!videoId.isNullOrBlank()) {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.youtube.com/watch?v=$videoId")
                                )
                                context.startActivity(intent)
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = video.snippet.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = video.snippet.channelTitle,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = video.snippet.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Abrir en YouTube",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
