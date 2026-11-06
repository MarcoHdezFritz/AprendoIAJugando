package cl.triskeledu.aprendoiajugando.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    val navy = Color(0xFF102A43)
    val blue = Color(0xFF2878D0)
    val background = Color(0xFFF3F7FC)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🤖", fontSize = 76.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "TRISKELEDU",
            color = blue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Aprendo IA\nJugando en Santiago",
            color = navy,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 39.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Explora Santiago, descubre códigos QR y aprende sobre Inteligencia Artificial mientras juegas.",
            color = Color.DarkGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue
            )
        ) {
            Text(
                "COMENZAR",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Aprende · Explora · Descubre",
            color = Color.Gray,
            fontSize = 13.sp
        )
    }
}
