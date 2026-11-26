package cl.triskeledu.aprendoiajugando.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.triskeledu.aprendoiajugando.viewmodel.UsuarioViewModel

@Composable
fun AdminUsersScreen(
    onBack: () -> Unit,
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    val uiState by usuarioViewModel.uiState.collectAsState()

    var usuarioAEliminar by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        usuarioViewModel.cargarUsuarios()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "GESTIÓN DE USUARIOS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF102A43)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Administra los usuarios registrados en la aplicación.",
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Usuarios: ${uiState.usuarios.size}",
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { usuarioViewModel.cargarUsuarios() },
                enabled = !uiState.isLoading
            ) {
                Text("Actualizar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        uiState.successMessage?.let {
            Text(it, color = Color(0xFF218838))
        }

        uiState.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.usuarios.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay usuarios registrados.",
                        color = Color.Gray
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.usuarios,
                        key = { it.id ?: it.hashCode().toLong() }
                    ) { usuario ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = usuario.nombre,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text("ID: ${usuario.id ?: "Sin ID"}")
                                Text("Alias: ${usuario.alias}")
                                Text("Teléfono: ${usuario.telefono}")
                                Text("Perfil: ${usuario.tipoPerfil}")

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        usuarioAEliminar = usuario.id
                                    },
                                    enabled = usuario.id != null,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC62828)
                                    )
                                ) {
                                    Text("Eliminar usuario")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("VOLVER AL PANEL")
        }
    }

    if (usuarioAEliminar != null) {
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Eliminar usuario") },
            text = {
                Text("¿Estás seguro de que deseas eliminar este usuario?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        usuarioAEliminar?.let {
                            usuarioViewModel.eliminarUsuario(it)
                        }
                        usuarioAEliminar = null
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { usuarioAEliminar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
