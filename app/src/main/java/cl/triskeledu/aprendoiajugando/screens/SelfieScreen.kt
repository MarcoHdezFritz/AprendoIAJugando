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

@Composable
fun SelfieScreen(
    onCapture: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isTakingPhoto by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

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
                                context as androidx.lifecycle.LifecycleOwner,
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
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val capture = imageCapture

                if (capture == null || isTakingPhoto) {
                    return@Button
                }

                isTakingPhoto = true

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
                                onCapture(uri.toString())
                            } else {
                                cameraError =
                                    "La foto se guardó, pero no se pudo obtener su ubicación."
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            isTakingPhoto = false
                            cameraError =
                                "Error al tomar la fotografía: ${exception.message}"
                        }
                    }
                )
            },
            enabled = hasPermission && imageCapture != null && !isTakingPhoto,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isTakingPhoto) "Guardando fotografía..."
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
