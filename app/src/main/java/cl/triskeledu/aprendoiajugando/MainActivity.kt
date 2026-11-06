
package cl.triskeledu.aprendoiajugando

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cl.triskeledu.aprendoiajugando.database.PlayerDatabaseHelper
import cl.triskeledu.aprendoiajugando.screens.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                val database = remember {
                    PlayerDatabaseHelper(applicationContext)
                }

                var screen by remember { mutableStateOf("welcome") }
                var playerName by remember { mutableStateOf("Jugador") }
                var playerEmail by remember { mutableStateOf("") }
                var currentPoint by remember { mutableStateOf("Plaza de Armas") }

                var videoTitle by remember {
                    mutableStateOf("Introducción a la Inteligencia Artificial")
                }

                var points by remember { mutableIntStateOf(0) }
                var scanSucceeded by remember { mutableStateOf(false) }

                var activities by remember {
                    mutableStateOf(emptyList<String>())
                }

                var discoveries by remember {
                    mutableStateOf(emptyList<String>())
                }

                fun refreshPlayerData() {
                    if (playerEmail.isNotBlank()) {
                        points = database.getPlayerPoints(playerEmail)

                        activities = database.getPlayerHistory(playerEmail)
                            .map {
                                "${it.description} (+${it.points} puntos)"
                            }

                        discoveries = database.getPlayerCollection(playerEmail)
                            .map { it.name }
                    }
                }

                LaunchedEffect(screen, playerEmail) {
                    if (screen == "progress" && playerEmail.isNotBlank()) {
                        points = database.getPlayerPoints(playerEmail)
                    }
                }

                when (screen) {

                    // BIENVENIDA
                    "welcome" -> WelcomeScreen {
                        screen = "selection"
                    }

                    // SELECCIÓN DE JUGADOR
                    "selection" -> PlayerSelectionScreen(
                        onRegister = { screen = "register" },
                        onLogin = { screen = "login" },
                        onBack = { screen = "welcome" }
                    )

                    // REGISTRO
                    "register" -> RegisterScreen(
                        onBack = { screen = "selection" },
                        onRegister = { name, email, phone, address, password ->

                            Thread {
                                try {
                                    val normalizedEmail =
                                        email.trim().lowercase()

                                    val created = database.registerPlayer(
                                        name,
                                        normalizedEmail,
                                        phone,
                                        address,
                                        password
                                    )

                                    if (created) {

                                        val newPoints =
                                            database.getPlayerPoints(normalizedEmail)

                                        val newActivities =
                                            database.getPlayerHistory(normalizedEmail)
                                                .map {
                                                    "${it.description} (+${it.points} puntos)"
                                                }

                                        val newDiscoveries =
                                            database.getPlayerCollection(normalizedEmail)
                                                .map { it.name }

                                        runOnUiThread {
                                            playerName = name
                                            playerEmail = normalizedEmail
                                            points = newPoints
                                            activities = newActivities
                                            discoveries = newDiscoveries
                                            screen = "dashboard"
                                        }

                                    } else {
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Este correo ya está registrado.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                } catch (e: Exception) {
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Error al registrar: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }.start()
                        }
                    )

                    // INICIO DE SESIÓN
                    "login" -> LoginScreen(
                        onLogin = { email, password ->

                            val normalizedEmail =
                                email.trim().lowercase()

                            when (normalizedEmail) {

                                "admin" -> {
                                    if (password == "123") {
                                        playerName = "Administrador"
                                        playerEmail = ""
                                        screen = "admin"
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Contraseña incorrecta.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                "gencont" -> {
                                    if (password == "123") {
                                        playerName = "Contenido"
                                        playerEmail = ""
                                        screen = "creator"
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Contraseña incorrecta.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                else -> {
                                    Thread {
                                        try {
                                            val authenticatedName =
                                                database.authenticatePlayer(
                                                    normalizedEmail,
                                                    password
                                                )

                                            if (authenticatedName != null) {

                                                val newPoints =
                                                    database.getPlayerPoints(
                                                        normalizedEmail
                                                    )

                                                val newActivities =
                                                    database.getPlayerHistory(
                                                        normalizedEmail
                                                    ).map {
                                                        "${it.description} (+${it.points} puntos)"
                                                    }

                                                val newDiscoveries =
                                                    database.getPlayerCollection(
                                                        normalizedEmail
                                                    ).map { it.name }

                                                runOnUiThread {
                                                    playerName = authenticatedName
                                                    playerEmail = normalizedEmail
                                                    points = newPoints
                                                    activities = newActivities
                                                    discoveries = newDiscoveries
                                                    screen = "dashboard"
                                                }

                                            } else {
                                                runOnUiThread {
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Correo o contraseña incorrectos.",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }

                                        } catch (e: Exception) {
                                            runOnUiThread {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Error al iniciar sesión: ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }.start()
                                }
                            }
                        },
                        onRegister = { screen = "register" },
                        onBack = { screen = "selection" }
                    )

                    // DASHBOARD
                    "dashboard" -> DashboardScreen(
                        playerName = playerName,
                        points = points,
                        onNavigate = { destination ->
                            screen = destination
                        }
                    )

                    // MAPA
                    "map" -> MapScreen(
                        onPointClick = { name ->
                            currentPoint = name
                            screen = "pointDetail"
                        },
                        onBack = { screen = "dashboard" }
                    )

                    // DETALLE DEL PUNTO
                    "pointDetail" -> PointDetailScreen(
                        pointName = currentPoint,
                        onScan = { screen = "scanner" },
                        onBack = { screen = "dashboard" }
                    )


                    // ESCÁNER QR
                    "scanner" -> QRScannerScreen(
                        onScan = { qrValue ->

                            val point = database.getTouristPoints()
                                .firstOrNull {
                                    it.qrCode == qrValue &&
                                            it.name == currentPoint
                                }

                            scanSucceeded =
                                if (point != null && playerEmail.isNotBlank()) {
                                    database.registerScan(
                                        playerEmail,
                                        point.id
                                    )
                                } else {
                                    false
                                }

                            refreshPlayerData()
                            screen = "result"
                        },
                        onBack = {
                            screen = "pointDetail"
                        }
                    )


                    // RESULTADO DEL ESCANEO
                    "result" -> QRResultScreen(
                        pointName = currentPoint,
                        onContinue = {
                            videoTitle =
                                "Inteligencia Artificial en $currentPoint"
                            screen = "video"
                        },
                        onBack = { screen = "dashboard" },
                        resultMessage = if (scanSucceeded) {
                            "¡Nuevo descubrimiento registrado!"
                        } else {
                            "Este código ya fue descubierto o no se pudo registrar."
                        },
                        pointsEarned = if (scanSucceeded) 10 else 0
                    )

                    // VÍDEO
                    "video" -> VideoScreen(
                        videoTitle = videoTitle,
                        onBack = { screen = "dashboard" }
                    )


                    // SELFIE
                    "selfie" -> SelfieScreen(
                        onCapture = { imagePath ->

                            val point = database.getTouristPoints()
                                .firstOrNull {
                                    it.name == currentPoint
                                }

                            val registered = database.registerSelfie(
                                playerEmail,
                                point?.id,
                                imagePath
                            )

                            refreshPlayerData()

                            Toast.makeText(
                                this@MainActivity,
                                if (registered) {
                                    "¡Selfie registrada! Has ganado 100 puntos."
                                } else {
                                    "Ya registraste una selfie en este lugar o ocurrió un error."
                                },
                                Toast.LENGTH_LONG
                            ).show()

                            screen = "dashboard"
                        },
                        onBack = {
                            screen = "dashboard"
                        }
                    )


                    // COLECCIÓN
                    "collection" -> CollectionScreen(
                        onBack = { screen = "dashboard" },
                        discoveries = discoveries
                    )

                    // PROGRESO
                    "progress" -> ProgressScreen(
                        points = points,
                        onBack = { screen = "dashboard" }
                    )

                    // HISTORIAL
                    "history" -> HistoryScreen(
                        activities = activities,
                        onBack = { screen = "dashboard" }
                    )

                    // NOTIFICACIONES
                    "notifications" -> NotificationsScreen(
                        onBack = { screen = "dashboard" }
                    )

                    // PERFIL
                    "profile" -> ProfileScreen(
                        playerName = playerName,
                        onSave = { newName ->
                            if (playerEmail.isNotBlank()) {
                                database.updatePlayerName(
                                    playerEmail,
                                    newName
                                )
                            }

                            playerName = newName
                        },
                        onBack = { screen = "dashboard" }
                    )

                    // ADMINISTRADOR
                    "admin" -> AdminScreen(
                        onManagePoints = {
                            screen = "managePoints"
                        },
                        onManageContent = {
                            screen = "manageVideos"
                        },
                        onBack = { screen = "selection" }
                    )

                    // CREADOR DE CONTENIDO
                    "creator" -> ContentCreatorScreen(
                        onManageVideos = {
                            screen = "manageVideos"
                        },
                        onBack = { screen = "selection" }
                    )

                    // GESTIÓN DE PUNTOS
                    "managePoints" -> ManagePointsScreen(
                        onBack = { screen = "admin" }
                    )

                    // GESTIÓN DE VÍDEOS
                    "manageVideos" -> ManageVideosScreen(
                        onBack = {
                            screen = if (playerName == "Contenido") {
                                "creator"
                            } else {
                                "admin"
                            }
                        }
                    )

                    // PANTALLA POR DEFECTO
                    else -> DashboardScreen(
                        playerName = playerName,
                        points = points,
                        onNavigate = { destination ->
                            screen = destination
                        }
                    )
                }
            }
        }
    }
}