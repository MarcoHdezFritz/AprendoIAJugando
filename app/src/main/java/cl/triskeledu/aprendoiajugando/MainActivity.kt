package cl.triskeledu.aprendoiajugando

import android.os.Bundle
import android.widget.Toast
import android.location.Location
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.triskeledu.aprendoiajugando.database.PlayerDatabaseHelper
import cl.triskeledu.aprendoiajugando.screens.*
import cl.triskeledu.aprendoiajugando.viewmodel.AuthViewModel
import cl.triskeledu.aprendoiajugando.repository.LugarRepository
import cl.triskeledu.aprendoiajugando.repository.ProductoRepository
import cl.triskeledu.aprendoiajugando.models.Producto
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                val scope = rememberCoroutineScope()

                var localNetworkPermissionGranted by remember {
                    mutableStateOf(
                        Build.VERSION.SDK_INT < 37 ||
                                checkSelfPermission("android.permission.ACCESS_LOCAL_NETWORK") ==
                                PackageManager.PERMISSION_GRANTED
                    )
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { granted ->
                    localNetworkPermissionGranted = granted
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= 37 &&
                        !localNetworkPermissionGranted
                    ) {
                        permissionLauncher.launch(
                            "android.permission.ACCESS_LOCAL_NETWORK"
                        )
                    }
                }

                // Comprobar conexión con el microservicio de Lugares.
                LaunchedEffect(localNetworkPermissionGranted) {
                    if (localNetworkPermissionGranted) {
                        val result = LugarRepository().listarLugaresActivos()

                        if (result.isSuccess) {
                            val lugares = result.getOrNull().orEmpty()

                            val mensaje = if (lugares.isEmpty()) {
                                "Conexión exitosa, pero no hay lugares activos."
                            } else {
                                "¡Conexión exitosa! Lugares recibidos: ${lugares.size}"
                            }

                            Toast.makeText(
                                this@MainActivity,
                                mensaje,
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Error: ${result.exceptionOrNull()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }


                val authViewModel: AuthViewModel = viewModel(
                    factory = remember(application) {
                        object : ViewModelProvider.Factory {

                            override fun <T : ViewModel> create(
                                modelClass: Class<T>
                            ): T {

                                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

                                    @Suppress("UNCHECKED_CAST")
                                    return AuthViewModel(application) as T
                                }

                                throw IllegalArgumentException(
                                    "ViewModel desconocido: ${modelClass.name}"
                                )
                            }
                        }
                    }
                )
                val authState by authViewModel.uiState.collectAsStateWithLifecycle()

                val database = remember {
                    PlayerDatabaseHelper(applicationContext)
                }

                var screen by remember { mutableStateOf("welcome") }
                var playerName by remember { mutableStateOf("Jugador") }
                var playerEmail by remember { mutableStateOf("") }
                var currentPoint by remember { mutableStateOf("Plaza de Armas") }
                var currentPointDescription by remember { mutableStateOf("") }
                var isLoadingPointDescription by remember { mutableStateOf(false) }
                var scannerReturnScreen by remember { mutableStateOf("dashboard") }

                var videoTitle by remember {
                    mutableStateOf("Introducción a la Inteligencia Artificial")
                }

                var videoReturnScreen by remember {
                    mutableStateOf("result")
                }

                // Producto recuperado desde el microservicio.
                var selectedProduct by remember {
                    mutableStateOf<Producto?>(null)
                }

                var points by remember { mutableIntStateOf(0) }
                var scanSucceeded by remember { mutableStateOf(false) }
                var scanProcessing by remember { mutableStateOf(false) }

                var scanMessage by remember {
                    mutableStateOf("Código QR descubierto correctamente.")
                }

                var activities by remember {
                    mutableStateOf(emptyList<String>())
                }

                var discoveries by remember {
                    mutableStateOf(emptyList<String>())
                }

                // Datos temporales para completar el proceso de autenticación.
                var pendingAuthEmail by remember { mutableStateOf("") }
                var pendingRegistrationName by remember { mutableStateOf("") }
                var pendingRegistrationEmail by remember { mutableStateOf("") }

                // Cargar información del jugador desde SQLite.
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

                // Recuperar la descripción del lugar seleccionado.
                LaunchedEffect(screen, currentPoint) {
                    if (screen == "pointDetail") {
                        isLoadingPointDescription = true
                        currentPointDescription = ""

                        try {
                            val result = LugarRepository().listarLugaresActivos()

                            if (result.isSuccess) {
                                val lugar = result.getOrNull()
                                    .orEmpty()
                                    .firstOrNull {
                                        it.activo &&
                                                it.nombre.equals(currentPoint, ignoreCase = true)
                                    }

                                currentPointDescription = lugar?.descripcion.orEmpty()
                            }
                        } catch (e: Exception) {
                            currentPointDescription = ""
                        } finally {
                            isLoadingPointDescription = false
                        }
                    }
                }

                // Observar los resultados del AuthViewModel.
                LaunchedEffect(authState) {

                    when {

                        authState.registrationSuccess -> {

                            playerName = pendingRegistrationName
                            playerEmail = pendingRegistrationEmail

                            refreshPlayerData()

                            screen = "dashboard"

                            authViewModel.clearState()
                        }

                        authState.authenticatedName != null -> {

                            playerName = authState.authenticatedName ?: "Jugador"

                            when (authState.authenticatedRole) {

                                "CLIENTE" -> {
                                    playerEmail = authState.authenticatedAlias
                                        ?: pendingAuthEmail

                                    refreshPlayerData()
                                    screen = "dashboard"
                                }

                                "ADMINISTRADOR" -> {
                                    playerEmail = ""
                                    points = 0
                                    activities = emptyList()
                                    discoveries = emptyList()
                                    screen = "admin"
                                }

                                "CREADOR_CONTENIDO" -> {
                                    playerEmail = ""
                                    points = 0
                                    activities = emptyList()
                                    discoveries = emptyList()
                                    screen = "creator"
                                }

                                else -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Tipo de usuario no reconocido.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    screen = "selection"
                                }
                            }

                            authViewModel.clearState()
                        }

                        authState.profileUpdateSuccess -> {

                            playerName = authState.updatedName ?: playerName

                            Toast.makeText(
                                this@MainActivity,
                                "¡Perfil actualizado correctamente!",
                                Toast.LENGTH_LONG
                            ).show()

                            authViewModel.clearState()
                        }

                        authState.errorMessage != null -> {

                            Toast.makeText(
                                this@MainActivity,
                                authState.errorMessage,
                                Toast.LENGTH_LONG
                            ).show()

                            authViewModel.clearState()
                        }
                    }
                }

                // Actualizar puntos al entrar a progreso.
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
                        onBack = {
                            screen = "selection"
                        },
                        onRegister = { name, email, phone, address, password ->

                            val normalizedEmail = email.trim().lowercase()

                            pendingRegistrationName = name
                            pendingRegistrationEmail = normalizedEmail

                            authViewModel.register(
                                name,
                                normalizedEmail,
                                phone,
                                address,
                                password
                            )
                        }
                    )

                    // INICIO DE SESIÓN
                    "login" -> LoginScreen(
                        onLogin = { email, password ->

                            val input = email.trim()

                            val normalizedAlias = when (input.lowercase()) {
                                "admin" -> "Admin"
                                "gencont" -> "GenCont"
                                else -> input.lowercase()
                            }

                            pendingAuthEmail = normalizedAlias

                            authViewModel.login(
                                normalizedAlias,
                                password
                            )
                        },

                        onRegister = {
                            screen = "register"
                        },

                        onBack = {
                            screen = "selection"
                        }
                    )

                    // DASHBOARD
                    "dashboard" -> DashboardScreen(
                        playerName = playerName,
                        points = points,
                        onNavigate = { destination ->

                            if (destination == "scanner") {
                                scannerReturnScreen = "dashboard"
                            }

                            screen = destination
                        }
                    )

                    // MAPA
                    "map" -> MapScreen(
                        onPointClick = { name ->
                            currentPoint = name
                            screen = "pointDetail"
                        },
                        onBack = {
                            screen = "dashboard"
                        }
                    )

                    // DETALLE DEL PUNTO
                    "pointDetail" -> PointDetailScreen(
                        pointName = currentPoint,
                        pointDescription = currentPointDescription,
                        isLoadingPointDescription = isLoadingPointDescription,

                        isUnlocked = discoveries.any {
                            it.equals(currentPoint, ignoreCase = true)
                        },

                        onScan = {
                            scannerReturnScreen = "pointDetail"
                            scanProcessing = false
                            screen = "scanner"
                        },


                        onWatchCapsule = {

                            // Evitar reutilizar un producto anterior.
                            selectedProduct = null

                            scope.launch {
                                try {
                                    // 1. Recuperar los lugares activos del microservicio.
                                    val lugaresResult =
                                        LugarRepository().listarLugaresActivos()

                                    if (lugaresResult.isFailure) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "No se pudieron consultar los lugares.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@launch
                                    }

                                    // 2. Buscar el lugar seleccionado en el mapa.
                                    val lugar = lugaresResult.getOrNull()
                                        .orEmpty()
                                        .firstOrNull {
                                            it.activo &&
                                                    it.nombre.equals(currentPoint, ignoreCase = true)
                                        }

                                    if (lugar == null) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "No se encontró el lugar seleccionado.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@launch
                                    }

                                    // 3. Obtener el ID de la cápsula asociada.
                                    val productoId = lugar.productoId

                                    if (productoId == null) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Este lugar todavía no tiene una cápsula asociada.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@launch
                                    }

                                    // 4. Recuperar la cápsula desde el microservicio Productos.
                                    val productoResult =
                                        ProductoRepository().buscarProducto(productoId)

                                    if (productoResult.isFailure) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "No se pudo recuperar la cápsula educativa.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@launch
                                    }

                                    val producto = productoResult.getOrNull()

                                    if (producto == null ||
                                        !producto.activo ||
                                        producto.urlVideo.isBlank()
                                    ) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "La cápsula no existe, está inactiva o no tiene URL.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@launch
                                    }

                                    // 5. Guardar el producto recuperado y abrir el video.
                                    selectedProduct = producto
                                    videoTitle = producto.titulo
                                    videoReturnScreen = "pointDetail"
                                    screen = "video"

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Error al recuperar la cápsula: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },

                        onBack = {
                            screen = "map"
                        }
                    )

                    // ESCÁNER QR CON CONSULTA A LOS MICROSERVICIOS
                    "scanner" -> QRScannerScreen(

                        onScan = { qrValue ->

                            // Evitar procesar varias veces el mismo escaneo.
                            if (!scanProcessing) {

                                scanProcessing = true
                                scanSucceeded = false
                                selectedProduct = null

                                scanMessage = "Consultando el código QR..."

                                scope.launch {

                                    try {

                                        if (playerEmail.isBlank()) {
                                            scanMessage =
                                                "Debes iniciar sesión como cliente para registrar el descubrimiento."

                                            screen = "result"
                                            return@launch
                                        }

                                        // 1. Buscar el lugar mediante el código QR.
                                        val lugarResult =
                                            LugarRepository().buscarLugarPorQr(
                                                qrValue.trim()
                                            )

                                        if (lugarResult.isFailure) {
                                            scanMessage =
                                                "No se encontró ningún lugar asociado a este código QR."

                                            screen = "result"
                                            return@launch
                                        }

                                        val lugar = lugarResult.getOrNull()

                                        if (lugar == null || !lugar.activo) {
                                            scanMessage =
                                                "El lugar no existe o se encuentra inactivo."

                                            screen = "result"
                                            return@launch
                                        }

                                        // 2. Comprobar que el lugar tenga un producto asociado.
                                        val productoId = lugar.productoId

                                        if (productoId == null) {
                                            scanMessage =
                                                "Este lugar todavía no tiene una cápsula educativa asociada."

                                            screen = "result"
                                            return@launch
                                        }

                                        // 3. Buscar la cápsula en el microservicio de Productos.
                                        val productoResult =
                                            ProductoRepository().buscarProducto(
                                                productoId
                                            )

                                        if (productoResult.isFailure) {
                                            scanMessage =
                                                "Se encontró el lugar, pero no fue posible recuperar su cápsula educativa."

                                            screen = "result"
                                            return@launch
                                        }

                                        val producto = productoResult.getOrNull()

                                        if (producto == null || !producto.activo) {
                                            scanMessage =
                                                "La cápsula educativa asociada no existe o está inactiva."

                                            screen = "result"
                                            return@launch
                                        }

                                        // 4. Actualizar el lugar y la cápsula seleccionados.
                                        currentPoint = lugar.nombre
                                        selectedProduct = producto
                                        videoTitle = producto.titulo

                                        // 5. Obtener o crear el punto turístico en SQLite.
                                        val localPointId = database.getOrCreateTouristPoint(lugar)

                                        if (localPointId == null) {
                                            scanMessage =
                                                "No fue posible registrar el lugar en la base de datos local."

                                            screen = "result"
                                            return@launch
                                        }

                                        // 6. Registrar el descubrimiento y sus 10 puntos.
                                        val registered = database.registerScan(
                                            playerEmail,
                                            localPointId
                                        )

                                        scanSucceeded = registered

                                        if (registered) {
                                            scanMessage =
                                                "¡Nuevo descubrimiento registrado! Has desbloqueado la cápsula de ${lugar.nombre}."
                                        } else {
                                            scanMessage =
                                                "El lugar y su cápsula fueron encontrados, pero este descubrimiento ya estaba registrado."
                                        }

                                        // 7. Actualizar los datos del jugador.
                                        refreshPlayerData()

                                        screen = "result"

                                    } catch (e: Exception) {

                                        scanSucceeded = false

                                        scanMessage =
                                            "Ocurrió un error al procesar el QR: ${e.message ?: "Error desconocido"}"

                                        screen = "result"

                                    } finally {
                                        scanProcessing = false
                                    }
                                }
                            }
                        },

                        onBack = {
                            scanProcessing = false
                            screen = scannerReturnScreen
                        }
                    )

                    // RESULTADO DEL ESCANEO
                    "result" -> QRResultScreen(
                        pointName = currentPoint,

                        onContinue = {
                            videoTitle = selectedProduct?.titulo
                                ?: "Cápsula turística: $currentPoint"

                            videoReturnScreen = "result"
                            screen = "video"
                        },

                        onBack = {
                            screen = "dashboard"
                        },

                        resultMessage = scanMessage,

                        pointsEarned = if (scanSucceeded) 10 else 0,

                        hasCapsule = !selectedProduct?.urlVideo.isNullOrBlank()
                    )

                    // VÍDEO
                    "video" -> VideoScreen(
                        videoTitle = videoTitle,
                        videoUrl = selectedProduct?.urlVideo.orEmpty(),
                        onBack = {
                            screen = videoReturnScreen
                        }
                    )

                    // SELFIE
                    "selfie" -> SelfieScreen(
                        onCapture = { imagePath, latitude, longitude ->

                            val touristPoints = database.getTouristPoints()
                                .filter { it.active }

                            val selectedPoint = touristPoints.firstOrNull {
                                it.name.equals(currentPoint, ignoreCase = true)
                            }

                            // Buscar el punto turístico más cercano.
                            val nearestPoint = touristPoints.minByOrNull { point ->
                                val results = FloatArray(1)

                                Location.distanceBetween(
                                    latitude,
                                    longitude,
                                    point.latitude,
                                    point.longitude,
                                    results
                                )

                                results[0]
                            }

                            var registered = false
                            var message: String

                            if (playerEmail.isBlank()) {
                                message =
                                    "Debes iniciar sesión para registrar una selfie."

                            } else if (selectedPoint == null || nearestPoint == null) {
                                message =
                                    "No se pudo identificar el punto turístico."

                            } else {
                                val distanceResults = FloatArray(1)

                                Location.distanceBetween(
                                    latitude,
                                    longitude,
                                    selectedPoint.latitude,
                                    selectedPoint.longitude,
                                    distanceResults
                                )

                                val distance = distanceResults[0]

                                if (nearestPoint.id != selectedPoint.id) {
                                    message =
                                        "Debes estar en el punto turístico seleccionado: ${nearestPoint.name} es el más cercano."

                                } else if (distance > 100f) {
                                    message =
                                        "Estás demasiado lejos de $currentPoint. Acércate para registrar tu selfie."

                                } else {
                                    registered = database.registerSelfie(
                                        playerEmail,
                                        selectedPoint.id,
                                        imagePath
                                    )

                                    message = if (registered) {
                                        "¡Selfie registrada! Has ganado 100 puntos."
                                    } else {
                                        "Ya registraste una selfie en este lugar o ocurrió un error."
                                    }
                                }
                            }

                            if (registered) {
                                refreshPlayerData()
                            }

                            Toast.makeText(
                                this@MainActivity,
                                message,
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
                        onBack = {
                            screen = "dashboard"
                        },
                        discoveries = discoveries
                    )

                    // PROGRESO
                    "progress" -> ProgressScreen(
                        points = points,
                        onBack = {
                            screen = "dashboard"
                        }
                    )

                    // HISTORIAL
                    "history" -> HistoryScreen(
                        activities = activities,
                        onBack = {
                            screen = "dashboard"
                        }
                    )

                    // NOTIFICACIONES
                    "notifications" -> NotificationsScreen(
                        onBack = {
                            screen = "dashboard"
                        }
                    )

                    // PERFIL
                    "profile" -> ProfileScreen(
                        playerName = playerName,

                        onSave = { newName ->
                            if (playerEmail.isNotBlank()) {
                                authViewModel.updateProfileName(
                                    playerEmail,
                                    newName
                                )
                            }
                        },

                        onBack = {
                            screen = "dashboard"
                        },

                        onLogout = {
                            playerName = "Jugador"
                            playerEmail = ""
                            points = 0
                            activities = emptyList()
                            discoveries = emptyList()
                            selectedProduct = null

                            authViewModel.clearState()

                            screen = "selection"
                        }
                    )

                    // ADMINISTRADOR
                    "admin" -> AdminScreen(
                        onManagePoints = {
                            screen = "managePoints"
                        },

                        onManageContent = {
                            screen = "manageVideos"
                        },

                        onManageUsers = {
                            screen = "manageUsers"
                        },

                        onBack = {
                            screen = "selection"
                        }
                    )

                    // CREADOR DE CONTENIDO
                    "creator" -> ContentCreatorScreen(
                        onManageVideos = {
                            screen = "manageVideos"
                        },

                        onBack = {
                            screen = "selection"
                        }
                    )

                    // GESTIÓN DE PUNTOS
                    "managePoints" -> ManagePointsScreen(
                        onBack = {
                            screen = "admin"
                        }
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

                    // GESTIÓN DE USUARIOS
                    "manageUsers" -> AdminUsersScreen(
                        onBack = {
                            screen = "admin"
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
