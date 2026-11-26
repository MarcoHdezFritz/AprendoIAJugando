
package cl.triskeledu.aprendoiajugando.screens

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun SelfieScreen(
    onCapture: (String, Double, Double) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    var isTakingPhoto by remember {
        mutableStateOf(false)
    }

    var cameraError by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selfie turística",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("¡Captura tu visita y consigue 100 puntos!")

        Spacer(modifier = Modifier.height(16.dp))

        if (hasPermission) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)

                    val cameraProviderFuture =
                        ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder()
                                .build()
                                .also {
                                    it.surfaceProvider =
                                        previewView.surfaceProvider
                                }

                            val capture = ImageCapture.Builder()
                                .setCaptureMode(
                                    ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                                )
                                .build()

                            cameraProvider.unbindAll()

                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_FRONT_CAMERA,
                                preview,
                                capture
                            )

                            imageCapture = capture

                        } catch (e: Exception) {
                            cameraError =
                                "No se pudo iniciar la cámara: ${e.message}"
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
        } else {
            Text("Se necesita permiso para utilizar la cámara.")

            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            ) {
                Text("Permitir cámara")
            }
        }

        cameraError?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (!hasLocationPermission) {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    return@Button
                }

                val capture = imageCapture

                if (capture == null || isTakingPhoto) {
                    return@Button
                }

                isTakingPhoto = true
                cameraError = null

                try {
                    val cancellationToken = CancellationTokenSource()

                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationToken.token
                    ).addOnSuccessListener { location ->

                        if (location == null) {
                            isTakingPhoto = false
                            cameraError =
                                "No se pudo obtener tu ubicación. Activa el GPS e inténtalo nuevamente."
                            return@addOnSuccessListener
                        }

                        if (location.accuracy > 50f) {
                            isTakingPhoto = false
                            cameraError =
                                "La ubicación no es suficientemente precisa. Inténtalo nuevamente en un lugar abierto."
                            return@addOnSuccessListener
                        }

                        val values = ContentValues().apply {
                            put(
                                MediaStore.Images.Media.DISPLAY_NAME,
                                "Selfie_Triskeledu_${System.currentTimeMillis()}.jpg"
                            )
                            put(
                                MediaStore.Images.Media.MIME_TYPE,
                                "image/jpeg"
                            )
                            put(
                                MediaStore.Images.Media.RELATIVE_PATH,
                                "Pictures/AprendoIAJugando"
                            )
                        }

                        val outputOptions =
                            ImageCapture.OutputFileOptions.Builder(
                                context.contentResolver,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values
                            ).build()

                        capture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {

                                override fun onImageSaved(
                                    outputFileResults: ImageCapture.OutputFileResults
                                ) {
                                    isTakingPhoto = false

                                    val uri = outputFileResults.savedUri

                                    if (uri != null) {
                                        onCapture(
                                            uri.toString(),
                                            location.latitude,
                                            location.longitude
                                        )
                                    } else {
                                        cameraError =
                                            "La foto se guardó, pero no se pudo obtener su ubicación."
                                    }
                                }

                                override fun onError(
                                    exception: ImageCaptureException
                                ) {
                                    isTakingPhoto = false
                                    cameraError =
                                        "Error al tomar la fotografía: ${exception.message}"
                                }
                            }
                        )
                    }.addOnFailureListener { exception ->
                        isTakingPhoto = false
                        cameraError =
                            "Error al obtener ubicación: ${exception.message}"
                    }

                } catch (e: SecurityException) {
                    isTakingPhoto = false
                    cameraError =
                        "No hay permiso para acceder a tu ubicación."
                }
            },
            enabled = hasPermission &&
                    imageCapture != null &&
                    !isTakingPhoto,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isTakingPhoto) "Obteniendo ubicación y guardando..."
                else "Tomar selfie"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
