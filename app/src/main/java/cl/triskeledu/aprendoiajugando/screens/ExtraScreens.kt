package cl.triskeledu.aprendoiajugando.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.triskeledu.aprendoiajugando.models.Lugar
import cl.triskeledu.aprendoiajugando.models.Producto
import cl.triskeledu.aprendoiajugando.viewmodel.LugarViewModel
import cl.triskeledu.aprendoiajugando.viewmodel.ProductoViewModel
import cl.triskeledu.aprendoiajugando.viewmodel.YouTubeViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val ExtraNavy = Color(0xFF102A43)
private val ExtraBlue = Color(0xFF2878D0)
private val ExtraBg = Color(0xFFF3F7FC)

@Composable
private fun ExtraContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ExtraBg)
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun ExtraTitle(title: String, subtitle: String) {
    Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ExtraNavy)
    Spacer(Modifier.height(8.dp))
    Text(subtitle, fontSize = 14.sp, color = Color.DarkGray)
    Spacer(Modifier.height(22.dp))
}

@Composable
private fun ExtraButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ExtraBlue)
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun ExtraCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = ExtraNavy, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(description, color = Color.DarkGray, fontSize = 14.sp)
        }
    }
}

// 7. COLECCIÓN


@Composable
fun CollectionScreen(
    onBack: () -> Unit,
    discoveries: List<String> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🏆 Mi colección",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Todos los lugares que has descubierto en tu aventura.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("🌎 Tus descubrimientos")
                Text(
                    text = "${discoveries.size}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Lugares registrados en tu colección")
            }
        }

        if (discoveries.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🗺️", style = MaterialTheme.typography.displayMedium)
                    Text(
                        "Tu aventura comienza aquí",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Explora Santiago y escanea los códigos QR de los puntos turísticos para llenar tu colección.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            discoveries.forEachIndexed { index, place ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                "📍",
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                place,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Lugar turístico descubierto",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            "✓",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Volver")
        }
    }
}

// 8. PROGRESO Y PUNTAJE

@Composable
fun ProgressScreen(
    points: Int,
    onBack: () -> Unit
) {
    val progress = points.coerceIn(0, 10000) / 10000f

    ExtraContainer {
        ExtraTitle("⭐ Mi progreso", "¡Sigue explorando y aprendiendo!")

        Text(
            "$points / 10000",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = ExtraBlue
        )

        Spacer(Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = ExtraBlue
        )

        Spacer(Modifier.height(20.dp))

        ExtraCard("🎯 Puntaje acumulado", "$points puntos obtenidos")
        ExtraCard("📷 Códigos QR", "Aquí aparecerá tu cantidad de códigos descubiertos.")
        ExtraCard("🤳 Selfies turísticas", "Aquí aparecerá tu cantidad de selfies registradas.")
        ExtraCard("🏅 Meta", "Tu objetivo es alcanzar los 10.000 puntos.")

        ExtraButton("VOLVER", onBack)
    }
}

// 9. HISTORIAL


@Composable
fun HistoryScreen(
    activities: List<String>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📜 Mi historial",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Revisa las actividades y recompensas de tu aventura.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("⭐ Actividades registradas")
                Text(
                    text = "${activities.size}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Tu progreso queda guardado en tu cuenta")
            }
        }

        if (activities.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📖", style = MaterialTheme.typography.displayMedium)
                    Text(
                        "Todavía no hay actividades",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Tus descubrimientos y selfies turísticas aparecerán aquí.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            activities.forEach { activity ->
                val isSelfie = activity.contains(
                    "SELFIE",
                    ignoreCase = true
                ) || activity.contains(
                    "selfie",
                    ignoreCase = true
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelfie)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else
                                MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                if (isSelfie) "📷" else "📍",
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (isSelfie) "Selfie turística"
                                else "Descubrimiento QR",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                activity,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Volver")
        }
    }
}

// 10. NOTIFICACIONES

@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    val navy = Color(0xFF102A43)
    val blue = Color(0xFF2878D0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F7FC))
            .padding(20.dp)
    ) {
        Text(
            "TRISKELEDU",
            color = blue,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "Notificaciones 🔔",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = navy
        )

        Text(
            "Mantente al día con tus aventuras.",
            color = Color.DarkGray
        )

        Spacer(Modifier.height(24.dp))

        val notifications = listOf(
            Triple("🎉", "¡Bienvenido!", "Gracias por formar parte de Aprendo IA Jugando."),
            Triple("📍", "Nuevos lugares", "Hay nuevos lugares turísticos por descubrir en Santiago."),
            Triple("🤖", "Aprende IA", "Sigue explorando y aprendiendo sobre inteligencia artificial."),
            Triple("🏆", "¡Sigue avanzando!", "Completa actividades para conseguir más puntos.")
        )

        notifications.forEach { notification ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(17.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        notification.first,
                        fontSize = 30.sp
                    )

                    Spacer(Modifier.width(14.dp))

                    Column {
                        Text(
                            notification.second,
                            color = navy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            notification.third,
                            color = Color.DarkGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = navy
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Volver al inicio")
        }
    }
}

// 11. PERFIL

@Composable
fun ProfileScreen(
    playerName: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var name by remember(playerName) { mutableStateOf(playerName) }
    var message by remember { mutableStateOf("") }

    ExtraContainer {
        ExtraTitle("👤 Mi perfil", "Administra tu información personal")

        Text("🧑", fontSize = 75.sp)

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                message = ""
            },
            label = { Text("Nombre del jugador") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(14.dp))

        ExtraCard(
            "⚙️ Configuración",
            "Desde aquí podrás administrar tus preferencias y permisos de la aplicación."
        )

        ExtraButton("GUARDAR CAMBIOS") {
            if (name.isBlank()) {
                message = "El nombre no puede estar vacío."
            } else {
                onSave(name.trim())
                message = "Cambios enviados para guardar."
            }
        }

        if (message.isNotEmpty()) {
            Text(message, color = ExtraBlue)
            Spacer(Modifier.height(10.dp))
        }

        ExtraButton("VOLVER", onBack)
        ExtraButton("CERRAR SESIÓN") { onLogout() }
    }
}

// 12. PANEL ADMINISTRADOR

@Composable
fun AdminScreen(
    onManagePoints: () -> Unit,
    onManageContent: () -> Unit,
    onManageUsers: () -> Unit,
    onBack: () -> Unit
) {
    ExtraContainer {
        ExtraTitle("🛠️ Administrador", "Panel de gestión de Triskeledu")

        ExtraCard("👥 Jugadores", "Consulta y administra los perfiles registrados.")
        ExtraCard("📍 Puntos turísticos", "Gestiona los lugares y códigos QR de la aplicación.")
        ExtraCard("📊 Contenido", "Administra las cápsulas educativas disponibles.")

        ExtraButton("GESTIONAR USUARIOS", onManageUsers)
        ExtraButton("GESTIONAR PUNTOS QR", onManagePoints)
        ExtraButton("GESTIONAR CONTENIDO", onManageContent)
        ExtraButton("VOLVER", onBack)
    }
}

// 13. PANEL CREADOR DE CONTENIDO

@Composable
fun ContentCreatorScreen(
    onManageVideos: () -> Unit,
    onBack: () -> Unit
) {
    ExtraContainer {
        ExtraTitle("🎬 Creador de contenido", "Panel de gestión educativa")

        ExtraCard("🤖 Cápsulas de IA", "Administra los vídeos y contenidos educativos.")
        ExtraCard("📱 Asociación QR", "Relaciona los códigos QR con sus respectivos contenidos.")
        ExtraCard("📚 Biblioteca", "Organiza los recursos educativos de la aplicación.")

        ExtraButton("GESTIONAR VÍDEOS", onManageVideos)
        ExtraButton("VOLVER", onBack)
    }
}

// 14. GESTIÓN DE PUNTOS QR

@Composable
fun ManagePointsScreen(
    onBack: () -> Unit,
    lugarViewModel: LugarViewModel = viewModel(),
    productoViewModel: ProductoViewModel = viewModel()
) {
    val uiState by lugarViewModel.uiState.collectAsState()
    val productoState by productoViewModel.uiState.collectAsState()

    var pointName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var qrCode by remember { mutableStateOf("") }

    var productoIdSeleccionado by remember { mutableStateOf<Long?>(null) }
    var selectorProductosAbierto by remember { mutableStateOf(false) }
    var selectedLugar by remember { mutableStateOf<Lugar?>(null) }
    var lugarAEliminar by remember { mutableStateOf<Lugar?>(null) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        lugarViewModel.cargarLugares()
        productoViewModel.cargarProductos()
    }

    fun limpiarFormulario() {
        pointName = ""
        description = ""
        latitude = ""
        longitude = ""
        qrCode = ""
        productoIdSeleccionado = null
        selectedLugar = null
        message = ""
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            limpiarFormulario()
        }
    }

    val productoSeleccionado = productoState.productos
        .firstOrNull { it.id == productoIdSeleccionado }

    ExtraContainer {
        ExtraTitle("📍 Gestión de puntos", "Administra los lugares turísticos")

        OutlinedTextField(
            value = pointName,
            onValueChange = { pointName = it },
            label = { Text("Nombre del lugar") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitud") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitud") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = qrCode,
            onValueChange = { qrCode = it },
            label = { Text("Código QR") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "🎬 Cápsula educativa asociada",
            color = ExtraNavy,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { selectorProductosAbierto = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(productoSeleccionado?.titulo ?: "Seleccionar cápsula")
            }

            DropdownMenu(
                expanded = selectorProductosAbierto,
                onDismissRequest = { selectorProductosAbierto = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                DropdownMenuItem(
                    text = { Text("Sin cápsula asociada") },
                    onClick = {
                        productoIdSeleccionado = null
                        selectorProductosAbierto = false
                    }
                )

                productoState.productos
                    .filter {
                        it.id != null && (it.activo || it.id == productoIdSeleccionado)
                    }
                    .forEach { producto ->
                        DropdownMenuItem(
                            text = { Text(producto.titulo) },
                            onClick = {
                                productoIdSeleccionado = producto.id
                                selectorProductosAbierto = false
                            }
                        )
                    }
            }
        }

        if (productoState.isLoading) {
            Text("Cargando cápsulas...", color = Color.Gray)
        }

        productoState.errorMessage?.let {
            Text("Error al cargar cápsulas: $it", color = Color.Red)
        }

        Spacer(Modifier.height(16.dp))

        ExtraButton(
            if (selectedLugar == null) "REGISTRAR PUNTO" else "GUARDAR CAMBIOS"
        ) {
            val lat = latitude.trim().replace(",", ".").toDoubleOrNull()
            val lon = longitude.trim().replace(",", ".").toDoubleOrNull()

            when {
                pointName.isBlank() || description.isBlank() ||
                        latitude.isBlank() || longitude.isBlank() || qrCode.isBlank() -> {
                    message = "Completa todos los campos."
                }

                lat == null || lat !in -90.0..90.0 -> {
                    message = "La latitud debe estar entre -90 y 90."
                }

                lon == null || lon !in -180.0..180.0 -> {
                    message = "La longitud debe estar entre -180 y 180."
                }

                else -> {
                    val lugar = Lugar(
                        id = selectedLugar?.id,
                        nombre = pointName.trim(),
                        descripcion = description.trim(),
                        latitud = lat,
                        longitud = lon,
                        altitud = selectedLugar?.altitud,
                        qrCodigo = qrCode.trim(),
                        productoId = productoIdSeleccionado,
                        activo = selectedLugar?.activo ?: true
                    )

                    val lugarActual = selectedLugar

                    if (lugarActual?.id != null) {
                        lugarViewModel.actualizarLugar(lugarActual.id, lugar)
                    } else {
                        lugarViewModel.crearLugar(lugar)
                    }

                    message = ""
                }
            }
        }

        if (selectedLugar != null) {
            ExtraButton("CANCELAR EDICIÓN") { limpiarFormulario() }
        }

        if (message.isNotBlank()) {
            Text(message, color = Color.Red)
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(10.dp))
        }

        uiState.errorMessage?.let { Text(it, color = Color.Red) }

        uiState.successMessage?.let {
            Text(it, color = Color(0xFF16834A))
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "LUGARES REGISTRADOS (${uiState.lugares.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ExtraNavy
        )

        Spacer(Modifier.height(10.dp))

        ExtraButton("ACTUALIZAR LISTA") {
            lugarViewModel.cargarLugares()
            productoViewModel.cargarProductos()
        }

        if (uiState.lugares.isEmpty() && !uiState.isLoading) {
            ExtraCard(
                "No hay lugares registrados",
                "Los lugares creados en el backend aparecerán aquí."
            )
        }

        uiState.lugares.forEach { lugar ->
            val productoDelLugar = productoState.productos
                .firstOrNull { it.id == lugar.productoId }

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        lugar.nombre,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExtraNavy
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(lugar.descripcion)
                    Text("📍 ${lugar.latitud}, ${lugar.longitud}")
                    Text("QR: ${lugar.qrCodigo}")

                    Text(
                        if (lugar.activo) "Estado: Activo" else "Estado: Inactivo"
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        productoDelLugar?.let { "🎬 Cápsula: ${it.titulo}" }
                            ?: if (lugar.productoId != null) {
                                "🎬 Cápsula ID: ${lugar.productoId} (no encontrada en la lista)"
                            } else {
                                "🎬 Sin cápsula asociada"
                            },
                        color = ExtraBlue,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(8.dp))

                    ExtraButton("EDITAR") {
                        selectedLugar = lugar
                        pointName = lugar.nombre
                        description = lugar.descripcion
                        latitude = lugar.latitud.toString()
                        longitude = lugar.longitud.toString()
                        qrCode = lugar.qrCodigo
                        productoIdSeleccionado = lugar.productoId
                        message = ""
                    }

                    ExtraButton("ELIMINAR") {
                        lugarAEliminar = lugar
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        ExtraButton("VOLVER", onBack)
    }

    lugarAEliminar?.let { lugar ->
        AlertDialog(
            onDismissRequest = { lugarAEliminar = null },
            title = { Text("Eliminar lugar") },
            text = { Text("¿Seguro que deseas eliminar ${lugar.nombre}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        lugar.id?.let { lugarViewModel.eliminarLugar(it) }
                        lugarAEliminar = null

                        if (selectedLugar?.id == lugar.id) {
                            limpiarFormulario()
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { lugarAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// 15. GESTIÓN DE VÍDEOS


@Composable
fun ManageVideosScreen(
    onBack: () -> Unit,
    productoViewModel: ProductoViewModel = viewModel(),
    youtubeViewModel: YouTubeViewModel = viewModel()
) {
    val uiState by productoViewModel.uiState.collectAsState()
    val videos by youtubeViewModel.videos.collectAsState()
    val cargandoVideos by youtubeViewModel.cargando.collectAsState()
    val errorYouTube by youtubeViewModel.error.collectAsState()

    var videoName by remember { mutableStateOf("") }
    var tema by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }

    var consultaYouTube by remember { mutableStateOf("") }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    var message by remember { mutableStateOf("") }

    fun limpiarFormulario() {
        videoName = ""
        tema = ""
        description = ""
        categoria = ""
        videoUrl = ""
        activo = true
        selectedProducto = null
        message = ""
    }

    fun esUrlValida(url: String): Boolean {
        return try {
            val uri = Uri.parse(url.trim())
            (uri.scheme == "https" || uri.scheme == "http") &&
                    !uri.host.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }

    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            limpiarFormulario()
        }
    }

    ExtraContainer {
        ExtraTitle(
            "🎥 Gestión de cápsulas",
            "Administra los contenidos educativos"
        )

        // BÚSQUEDA DE YOUTUBE

        ExtraCard(
            "🔎 Buscar en YouTube",
            "Busca un video educativo y selecciónalo para utilizarlo como cápsula."
        )

        OutlinedTextField(
            value = consultaYouTube,
            onValueChange = { consultaYouTube = it },
            label = { Text("Buscar videos en YouTube") },
            placeholder = { Text("Ej: Introducción a la inteligencia artificial") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        ExtraButton("BUSCAR VIDEOS") {
            if (consultaYouTube.isBlank()) {
                message = "Escribe algo para buscar en YouTube."
            } else {
                message = ""
                youtubeViewModel.buscarVideos(consultaYouTube.trim())
            }
        }

        if (cargandoVideos) {
            CircularProgressIndicator()
            Spacer(Modifier.height(10.dp))
            Text("Buscando videos en YouTube...")
        }

        errorYouTube?.let {
            Text(
                "Error de YouTube: $it",
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(10.dp))
        }

        if (!cargandoVideos && videos.isEmpty() && errorYouTube == null) {
            Text(
                "Los resultados de búsqueda aparecerán aquí.",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }

        videos.forEach { video ->
            val videoId = video.id.videoId

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        video.snippet.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExtraNavy
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        "Canal: ${video.snippet.channelTitle}",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        video.snippet.description,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        maxLines = 3
                    )

                    Spacer(Modifier.height(10.dp))

                    if (!videoId.isNullOrBlank()) {
                        ExtraButton("SELECCIONAR ESTE VIDEO") {
                            videoName = video.snippet.title
                            tema = consultaYouTube.trim()
                            description = video.snippet.description
                            categoria = "Educación"
                            videoUrl = "https://www.youtube.com/watch?v=$videoId"
                            selectedProducto = null
                            activo = true
                            message = "Video seleccionado. Revisa los datos y registra la cápsula."
                        }
                    } else {
                        Text(
                            "Este resultado no tiene un ID de video válido.",
                            color = Color.Red
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 18.dp)
        )

        // FORMULARIO DE LA CÁPSULA

        Text(
            "📝 DATOS DE LA CÁPSULA",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ExtraNavy
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = videoName,
            onValueChange = { videoName = it },
            label = { Text("Título de la cápsula") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = tema,
            onValueChange = { tema = it },
            label = { Text("Tema") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = videoUrl,
            onValueChange = { videoUrl = it },
            label = { Text("URL del vídeo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        if (videoUrl.isNotBlank()) {
            Text(
                "La URL seleccionada se guardará en el producto.",
                color = ExtraBlue,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cápsula activa",
                color = ExtraNavy,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = activo,
                onCheckedChange = { activo = it }
            )
        }

        Spacer(Modifier.height(14.dp))

        ExtraButton(
            if (selectedProducto == null) {
                "REGISTRAR CÁPSULA"
            } else {
                "GUARDAR CAMBIOS"
            }
        ) {
            when {
                videoName.isBlank() ||
                        tema.isBlank() ||
                        description.isBlank() ||
                        categoria.isBlank() ||
                        videoUrl.isBlank() -> {
                    message = "Completa todos los campos."
                }

                !esUrlValida(videoUrl) -> {
                    message = "Ingresa una URL válida con http:// o https://."
                }

                else -> {
                    val producto = Producto(
                        id = selectedProducto?.id,
                        titulo = videoName.trim(),
                        tema = tema.trim(),
                        descripcion = description.trim(),
                        categoria = categoria.trim(),
                        urlVideo = videoUrl.trim(),
                        activo = activo
                    )

                    val id = selectedProducto?.id

                    if (id == null) {
                        productoViewModel.crearProducto(producto)
                    } else {
                        productoViewModel.actualizarProducto(id, producto)
                    }

                    message = ""
                }
            }
        }

        if (selectedProducto != null) {
            ExtraButton("CANCELAR EDICIÓN") {
                limpiarFormulario()
            }
        }

        if (message.isNotBlank()) {
            Text(message, color = ExtraBlue)
            Spacer(Modifier.height(10.dp))
        }

        uiState.errorMessage?.let {
            Text(it, color = Color.Red)
            Spacer(Modifier.height(10.dp))
        }

        uiState.successMessage?.let {
            Text(it, color = Color(0xFF16834A))
            Spacer(Modifier.height(10.dp))
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 18.dp)
        )

        Text(
            "CÁPSULAS REGISTRADAS (${uiState.productos.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ExtraNavy
        )

        Spacer(Modifier.height(10.dp))

        ExtraButton("ACTUALIZAR LISTA") {
            productoViewModel.cargarProductos()
        }

        if (uiState.productos.isEmpty() && !uiState.isLoading) {
            ExtraCard(
                "No hay cápsulas registradas",
                "Las cápsulas creadas en el backend aparecerán aquí."
            )
        }

        uiState.productos.forEach { producto ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        producto.titulo,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExtraNavy
                    )

                    Spacer(Modifier.height(6.dp))

                    Text("Tema: ${producto.tema}")
                    Text("Categoría: ${producto.categoria}")
                    Text("Descripción: ${producto.descripcion}")

                    Text(
                        "URL: ${producto.urlVideo}",
                        color = ExtraBlue,
                        fontSize = 12.sp
                    )

                    Text(
                        if (producto.activo) {
                            "Estado: Activo"
                        } else {
                            "Estado: Inactivo"
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    ExtraButton("EDITAR") {
                        selectedProducto = producto
                        videoName = producto.titulo
                        tema = producto.tema
                        description = producto.descripcion
                        categoria = producto.categoria
                        videoUrl = producto.urlVideo
                        activo = producto.activo
                        message = ""
                    }

                    ExtraButton("ELIMINAR") {
                        productoAEliminar = producto
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        ExtraButton("VOLVER", onBack)
    }

    productoAEliminar?.let { producto ->
        AlertDialog(
            onDismissRequest = {
                productoAEliminar = null
            },
            title = {
                Text("Eliminar cápsula")
            },
            text = {
                Text("¿Seguro que deseas eliminar ${producto.titulo}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        producto.id?.let {
                            productoViewModel.eliminarProducto(it)
                        }

                        if (selectedProducto?.id == producto.id) {
                            limpiarFormulario()
                        }

                        productoAEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        productoAEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
