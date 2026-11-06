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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF102A43)
private val Blue = Color(0xFF2878D0)
private val LightBg = Color(0xFFF3F7FC)
private val Green = Color(0xFF24A36A)

@Composable
private fun ScreenContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun ScreenTitle(title: String, subtitle: String) {
    Text(title, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Navy)
    Spacer(Modifier.height(8.dp))
    Text(subtitle, fontSize = 14.sp, color = Color.DarkGray,
        textAlign = TextAlign.Center)
    Spacer(Modifier.height(22.dp))
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Blue)
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
private fun InfoCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Navy)
            Spacer(Modifier.height(6.dp))
            Text(description, color = Color.DarkGray, fontSize = 14.sp)
        }
    }
}

// 2. DETALLE DEL PUNTO TURÍSTICO

@Composable
fun PointDetailScreen(
    pointName: String,
    onScan: () -> Unit,
    onBack: () -> Unit
) {
    ScreenContainer {
        ScreenTitle("📍 Detalle del lugar", pointName)

        Text("🏙️", fontSize = 76.sp)
        InfoCard(
            pointName,
            "Este lugar forma parte de la experiencia Aprendo IA Jugando en Santiago. Explora su historia y encuentra el código QR para descubrir contenido educativo."
        )
        InfoCard("🎯 Desafío disponible", "Escanea el código QR para desbloquear una cápsula educativa.")
        InfoCard("⭐ Recompensa", "Obtén 10 puntos por descubrir este código QR.")

        ActionButton("ESCANEAR CÓDIGO QR", onScan)
        ActionButton("VOLVER", onBack)
    }
}

// 3. ESCÁNER QR

@Composable
fun QRScannerScreen(
    onScan: () -> Unit,
    onBack: () -> Unit
) {
    ScreenContainer {
        ScreenTitle("📷 Escáner QR", "Apunta la cámara hacia el código del lugar")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Navy, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("▦", fontSize = 100.sp, color = Color.White)
                Text("Área de escaneo", color = Color.White, fontSize = 17.sp)
                Text("Cámara pendiente de integración", color = Color.LightGray, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
        InfoCard("¿Cómo funciona?", "Escanea un código QR turístico para desbloquear contenido educativo y ganar puntos.")
        ActionButton("SIMULAR ESCANEO EXITOSO", onScan)
        ActionButton("VOLVER", onBack)
    }
}

// 4. RESULTADO DEL ESCANEO

@Composable
fun QRResultScreen(
    pointName: String,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    resultMessage: String = "Código QR descubierto correctamente.",
    pointsEarned: Int = 10
) {
    ScreenContainer {
        ScreenTitle("📱 Resultado del escaneo", pointName)

        Text(
            if (pointsEarned > 0) "🎉" else "ℹ️",
            fontSize = 85.sp
        )

        Text(
            resultMessage,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = if (pointsEarned > 0) Green else Navy,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        InfoCard("📍 Lugar", pointName)
        InfoCard("⭐ Puntos obtenidos", "+$pointsEarned puntos")

        if (pointsEarned > 0) {
            InfoCard(
                "🤖 Contenido educativo",
                "Has desbloqueado una cápsula educativa sobre inteligencia artificial."
            )
            ActionButton("VER CONTENIDO", onContinue)
        }

        ActionButton("VOLVER AL INICIO", onBack)
    }
}

// 5. VÍDEO EDUCATIVO

@Composable
fun VideoScreen(
    videoTitle: String,
    onBack: () -> Unit
) {
    ScreenContainer {
        ScreenTitle("🎬 Cápsula educativa", videoTitle)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Navy, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("▶️", fontSize = 65.sp)
                Text("Reproductor de vídeo", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
        InfoCard(
            "🤖 Aprende sobre IA",
            "Descubre cómo funciona la inteligencia artificial, sus aplicaciones y su impacto en nuestra vida cotidiana."
        )
        InfoCard("📚 Tu aprendizaje", "Esta actividad quedará registrada en tu historial.")
        ActionButton("VOLVER", onBack)
    }
}

// 6. SELFIE TURÍSTICA

@Composable
fun SelfieScreen(
    onCapture: () -> Unit,
    onBack: () -> Unit
) {
    ScreenContainer {
        ScreenTitle("🤳 Selfie turística", "Guarda un recuerdo de tu visita")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Navy, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📸", fontSize = 85.sp)
                Text("Vista de cámara", color = Color.White, fontSize = 17.sp)
                Text("Cámara pendiente de integración", color = Color.LightGray, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
        InfoCard("⭐ Recompensa especial", "Obtén 100 puntos al completar una selfie turística válida.")
        ActionButton("SIMULAR CAPTURA", onCapture)
        ActionButton("VOLVER", onBack)
    }
}
