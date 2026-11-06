
package cl.triskeledu.aprendoiajugando.screens

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
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
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
    ExtraContainer {
        ExtraTitle("🏆 Mi colección", "Todos tus descubrimientos")

        if (discoveries.isEmpty()) {
            ExtraCard(
                "Todavía no tienes descubrimientos",
                "Escanea códigos QR en los lugares turísticos para comenzar tu colección."
            )
        } else {
            discoveries.forEach { place ->
                ExtraCard(
                    "📍 $place",
                    "Código QR descubierto y registrado."
                )
            }
        }

        ExtraButton("VOLVER", onBack)
    }
}

// 8. PROGRESO Y PUNTAJE

@Composable
fun ProgressScreen(
    points: Int,
    onBack: () -> Unit
) {
    val progress = (points.coerceIn(0, 10000) / 10000f)

    ExtraContainer {
        ExtraTitle("⭐ Mi progreso", "¡Sigue explorando y aprendiendo!")

        Text("$points / 10000", fontSize = 36.sp,
            fontWeight = FontWeight.Bold, color = ExtraBlue)

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
    ExtraContainer {
        ExtraTitle("📋 Mi historial", "Revisa tus últimas actividades")

        if (activities.isEmpty()) {
            ExtraCard("Todavía no hay actividades",
                "Cuando descubras códigos QR o completes desafíos, aparecerán aquí.")
        } else {
            activities.forEachIndexed { index, activity ->
                ExtraCard("Actividad ${index + 1}", activity)
            }
        }

        ExtraButton("VOLVER", onBack)
    }
}

// 10. NOTIFICACIONES

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    ExtraContainer {
        ExtraTitle("🔔 Notificaciones", "Mantente al día con tus desafíos")

        ExtraCard("🎉 ¡Bienvenido!", "Comienza tu aventura de aprendizaje en Santiago.")
        ExtraCard("📍 Nuevos lugares", "Explora los puntos turísticos disponibles en el mapa.")
        ExtraCard("🤖 Aprende IA", "Descubre nuevas cápsulas educativas y amplía tus conocimientos.")

        ExtraButton("VOLVER", onBack)
    }
}

// 11. PERFIL

@Composable
fun ProfileScreen(
    playerName: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit
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

        ExtraCard("⚙️ Configuración",
            "Desde aquí podrás administrar tus preferencias y permisos de la aplicación.")

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
    }
}

// 12. PANEL ADMINISTRADOR

@Composable
fun AdminScreen(
    onManagePoints: () -> Unit,
    onManageContent: () -> Unit,
    onBack: () -> Unit
) {
    ExtraContainer {
        ExtraTitle("🛠️ Administrador", "Panel de gestión de Triskeledu")

        ExtraCard("👥 Jugadores", "Administración de perfiles registrados en el dispositivo.")
        ExtraCard("📍 Puntos turísticos", "Gestiona los lugares y códigos QR de la aplicación.")
        ExtraCard("📊 Contenido", "Administra las cápsulas educativas disponibles.")

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
fun ManagePointsScreen(onBack: () -> Unit) {
    var pointName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    ExtraContainer {
        ExtraTitle("📍 Gestión de puntos", "Administración de lugares turísticos")

        OutlinedTextField(
            value = pointName,
            onValueChange = { pointName = it },
            label = { Text("Nombre del lugar") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción del lugar") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        ExtraButton("REGISTRAR PUNTO") {
            message = if (pointName.isBlank() || description.isBlank()) {
                "Completa todos los campos."
            } else {
                "Formulario validado. Pendiente de guardar en SQLite."
            }
        }

        if (message.isNotEmpty()) {
            Text(message, color = ExtraBlue)
            Spacer(Modifier.height(12.dp))
        }

        ExtraButton("VOLVER", onBack)
    }
}

// 15. GESTIÓN DE VÍDEOS

@Composable
fun ManageVideosScreen(onBack: () -> Unit) {
    var videoName by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    ExtraContainer {
        ExtraTitle("🎥 Gestión de vídeos", "Administra las cápsulas educativas")

        OutlinedTextField(
            value = videoName,
            onValueChange = { videoName = it },
            label = { Text("Nombre de la cápsula") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = videoUrl,
            onValueChange = { videoUrl = it },
            label = { Text("URL del vídeo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        ExtraButton("REGISTRAR VÍDEO") {
            message = if (videoName.isBlank() || videoUrl.isBlank()) {
                "Completa todos los campos."
            } else {
                "Formulario validado. Pendiente de guardar en SQLite."
            }
        }

        if (message.isNotEmpty()) {
            Text(message, color = ExtraBlue)
            Spacer(Modifier.height(12.dp))
        }

        ExtraButton("VOLVER", onBack)
    }
}
