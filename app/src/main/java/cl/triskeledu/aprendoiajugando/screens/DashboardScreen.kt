
package cl.triskeledu.aprendoiajugando.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    playerName: String,
    points: Int,
    onNavigate: (String) -> Unit
) {
    val navy = Color(0xFF102A43)
    val blue = Color(0xFF2878D0)

    var showProgress by remember { mutableStateOf(false) }
    var visibleCards by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(150)
        showProgress = true

        for (i in 1..7) {
            delay(100)
            visibleCards = i
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F7FC))
            .verticalScroll(rememberScrollState())
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
            "¡Hola, $playerName!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = navy
        )

        Text(
            "¿Qué aventura exploraremos hoy?",
            color = Color.DarkGray
        )

        Spacer(Modifier.height(24.dp))

        // TARJETA DE PROGRESO ANIMADA
        AnimatedVisibility(
            visible = showProgress,
            enter = fadeIn(tween(600)) +
                    scaleIn(
                        initialScale = 0.85f,
                        animationSpec = tween(600)
                    )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = navy
                )
            ) {
                Column(
                    Modifier.padding(22.dp)
                ) {
                    Text(
                        "⭐ TU PROGRESO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "$points puntos",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "¡Sigue descubriendo Santiago!",
                        color = Color.LightGray
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "EXPLORAR",
            color = navy,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        val options = listOf(
            Triple("🗺️", "Mapa de Santiago", "map"),
            Triple("📷", "Escanear código QR", "scanner"),
            Triple("🤳", "Selfie turística", "selfie"),
            Triple("🏆", "Mi colección", "collection"),
            Triple("📊", "Mi progreso", "progress"),
            Triple("📜", "Historial", "history"),
            Triple("👤", "Mi perfil", "profile")
        )

        options.forEachIndexed { index, option ->

            AnimatedVisibility(
                visible = visibleCards > index,
                enter = fadeIn(
                    animationSpec = tween(450)
                ) + slideInVertically(
                    animationSpec = tween(450),
                    initialOffsetY = { it / 2 }
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .animateContentSize(),
                    onClick = {
                        onNavigate(option.third)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(17.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            option.first,
                            fontSize = 25.sp
                        )

                        Spacer(Modifier.width(14.dp))

                        Text(
                            option.second,
                            color = navy,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
