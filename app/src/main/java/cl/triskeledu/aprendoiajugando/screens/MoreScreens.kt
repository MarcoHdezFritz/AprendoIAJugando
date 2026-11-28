package cl.triskeledu.aprendoiajugando.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.webkit.WebView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.View
import android.app.Activity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import android.content.Context
import android.content.ContextWrapper
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip

private val Navy = Color(0xFF102A43)
private val Blue = Color(0xFF2878D0)
private val LightBg = Color(0xFFF3F7FC)
private val Green = Color(0xFF24A36A)

private fun Context.findActivity(): Activity? {
    var currentContext = this

    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }

        currentContext = currentContext.baseContext
    }

    return currentContext as? Activity
}

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
    Text(
        title,
        fontSize = 27.sp,
        fontWeight = FontWeight.Bold,
        color = Navy
    )

    Spacer(Modifier.height(8.dp))

    Text(
        subtitle,
        fontSize = 14.sp,
        color = Color.DarkGray,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(22.dp))
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Navy
            )

            Spacer(Modifier.height(6.dp))

            Text(
                description,
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        }
    }
}

// 2. DETALLE DEL PUNTO TURÍSTICO


@Composable
fun PointDetailScreen(
    pointName: String,
    pointDescription: String,
    isLoadingPointDescription: Boolean,
    isUnlocked: Boolean,
    onScan: () -> Unit,
    onWatchCapsule: () -> Unit,
    onBack: () -> Unit
) {
    ScreenContainer {
        ScreenTitle("📍 Detalle del lugar", pointName)

        Text("🏙️", fontSize = 76.sp)

        if (!isLoadingPointDescription && pointDescription.isNotBlank()) {
            InfoCard(
                pointName,
                pointDescription
            )
        }
        if (isUnlocked) {
            InfoCard(
                "🎉 Lugar descubierto",
                "Ya desbloqueaste la cápsula turística de este lugar. ¡Puedes verla nuevamente cuando quieras!"
            )

            ActionButton("🎬 VER CÁPSULA", onWatchCapsule)
        } else {
            InfoCard(
                "🎯 Desafío disponible",
                "Escanea el código QR para desbloquear una cápsula educativa."
            )

            InfoCard(
                "⭐ Recompensa",
                "Obtén 10 puntos por descubrir este código QR."
            )

            ActionButton("ESCANEAR CÓDIGO QR", onScan)
        }

        ActionButton("VOLVER", onBack)
    }
}

// 3. ESCÁNER QR SIMULADO

@Composable
fun QRScannerScreen(
    onScan: () -> Unit,
    onBack: () -> Unit
) {
    ScreenContainer {
        ScreenTitle(
            "📷 Escáner QR",
            "Apunta la cámara hacia el código del lugar"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Navy, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("▦", fontSize = 100.sp, color = Color.White)

                Text(
                    "Área de escaneo",
                    color = Color.White,
                    fontSize = 17.sp
                )

                Text(
                    "Cámara pendiente de integración",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        InfoCard(
            "¿Cómo funciona?",
            "Escanea un código QR turístico para desbloquear contenido educativo y ganar puntos."
        )

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
    pointsEarned: Int = 10,
    hasCapsule: Boolean = false
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

        InfoCard(
            "⭐ Puntos obtenidos",
            "+$pointsEarned puntos"
        )

        if (hasCapsule) {
            InfoCard(
                "🤖 Cápsula educativa desbloqueada",
                "Puedes visualizar el contenido educativo asociado a este lugar."
            )

            ActionButton("🎬 VER CÁPSULA", onContinue)
        }

        ActionButton("VOLVER AL INICIO", onBack)
    }
}

// 5. VÍDEO EDUCATIVO


@Composable
fun VideoScreen(
    videoTitle: String,
    videoUrl: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val videoId = remember(videoUrl) {
        try {
            val uri = Uri.parse(videoUrl.trim())

            val id = when (uri.host?.lowercase()) {
                "youtu.be" -> uri.lastPathSegment

                "youtube.com",
                "www.youtube.com",
                "m.youtube.com",
                "music.youtube.com" -> {
                    when {
                        uri.path?.startsWith("/embed/") == true ->
                            uri.pathSegments.getOrNull(1)

                        uri.path?.startsWith("/shorts/") == true ->
                            uri.pathSegments.getOrNull(1)

                        uri.path?.startsWith("/live/") == true ->
                            uri.pathSegments.getOrNull(1)

                        else -> uri.getQueryParameter("v")
                    }
                }

                else -> null
            }

            id?.takeIf { it.matches(Regex("[A-Za-z0-9_-]{11}")) }

        } catch (e: Exception) {
            null
        }
    }

    val thumbnailUrl = videoId?.let {
        "https://img.youtube.com/vi/$it/hqdefault.jpg"
    }

    ScreenContainer {
        ScreenTitle("🎬 Cápsula turística", videoTitle)

        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "Miniatura del video $videoTitle",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            InfoCard(
                "⚠️ Miniatura no disponible",
                "No se pudo obtener la imagen del video."
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Has desbloqueado el contenido educativo asociado a este lugar turístico.",
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        InfoCard(
            "📚 Tu cápsula",
            videoTitle
        )

        InfoCard(
            "📍 Lugar asociado",
            "Esta cápsula corresponde al lugar que descubriste mediante su código QR."
        )

        InfoCard(
            "▶️ Ver contenido educativo",
            "Presiona el botón para abrir el video directamente en YouTube."
        )

        ActionButton("ABRIR VIDEO EN YOUTUBE") {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(videoUrl)
                )

                intent.setPackage("com.google.android.youtube")

                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    intent.setPackage(null)
                    context.startActivity(intent)
                }

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "No se pudo abrir el video.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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
        ScreenTitle(
            "🤳 Selfie turística",
            "Guarda un recuerdo de tu visita"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Navy, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📸", fontSize = 85.sp)

                Text(
                    "Vista de cámara",
                    color = Color.White,
                    fontSize = 17.sp
                )

                Text(
                    "Cámara pendiente de integración",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        InfoCard(
            "⭐ Recompensa especial",
            "Obtén 100 puntos al completar una selfie turística válida."
        )

        ActionButton("SIMULAR CAPTURA", onCapture)

        ActionButton("VOLVER", onBack)
    }
}
