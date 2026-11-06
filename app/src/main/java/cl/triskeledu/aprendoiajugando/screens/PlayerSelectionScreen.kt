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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerSelectionScreen(
    onRegister: () -> Unit,
    onLogin: () -> Unit,
    onBack: () -> Unit
) {
    val blue = Color(0xFF2878D0)
    val navy = Color(0xFF102A43)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F7FC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("👾", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "¿Quién eres?",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = navy
        )

        Text(
            "Selecciona cómo deseas ingresar",
            color = Color.DarkGray,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue
            )
        ) {
            Text("INICIAR SESIÓN", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("CREAR JUGADOR", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        TextButton(onClick = onBack) {
            Text("← Volver al inicio")
        }
    }
}
