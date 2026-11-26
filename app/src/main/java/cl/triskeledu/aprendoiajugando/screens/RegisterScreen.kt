
package cl.triskeledu.aprendoiajugando.screens

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegister: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }

    val blue = Color(0xFF2878D0)
    val navy = Color(0xFF102A43)

    // Validaciones individuales
    val nameInvalid = name.isBlank()

    val emailInvalid = !android.util.Patterns.EMAIL_ADDRESS
        .matcher(email.trim()).matches()

    val phoneInvalid = phone.isBlank() ||
            phone.count { it.isDigit() } < 8

    val addressInvalid = address.isBlank()

    val passwordInvalid = password.length < 6

    val formInvalid = nameInvalid ||
            emailInvalid ||
            phoneInvalid ||
            addressInvalid ||
            passwordInvalid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F7FC))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📝", fontSize = 55.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Crear jugador",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = navy
        )

        Text(
            "Completa tus datos para comenzar",
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showErrors && nameInvalid
        )

        if (showErrors && nameInvalid) {
            ErrorText("Ingresa tu nombre.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showErrors && emailInvalid
        )

        if (showErrors && emailInvalid) {
            ErrorText("Ingresa un correo electrónico válido.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Teléfono
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showErrors && phoneInvalid
        )

        if (showErrors && phoneInvalid) {
            ErrorText("Ingresa un teléfono con al menos 8 dígitos.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dirección
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showErrors && addressInvalid
        )

        if (showErrors && addressInvalid) {
            ErrorText("Ingresa tu dirección.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showErrors && passwordInvalid
        )

        if (showErrors && passwordInvalid) {
            ErrorText("La contraseña debe tener al menos 6 caracteres.")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                showErrors = true

                if (!formInvalid) {
                    onRegister(
                        name.trim(),
                        email.trim(),
                        phone.trim(),
                        address.trim(),
                        password
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue
            )
        ) {
            Text(
                "REGISTRARME",
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(onClick = onBack) {
            Text("← Volver")
        }
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = Color.Red,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 4.dp)
    )
}
