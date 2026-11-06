
package cl.triskeledu.aprendoiajugando.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.view.MotionEvent

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.core.content.ContextCompat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

import cl.triskeledu.aprendoiajugando.database.PlayerDatabaseHelper
import cl.triskeledu.aprendoiajugando.models.TouristPoint

// --------------------------------------------------
// MARCADORES PERSONALIZADOS
// --------------------------------------------------

private class TouristPointsOverlay(
    private val points: List<TouristPoint>,
    private val onPointClick: (String) -> Unit
) : Overlay() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(40, 120, 208)
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    override fun draw(
        canvas: Canvas,
        mapView: MapView,
        shadow: Boolean
    ) {
        super.draw(canvas, mapView, shadow)

        if (shadow) return

        val screenPoint = Point()

        points.forEach { point ->
            val geoPoint = GeoPoint(
                point.latitude,
                point.longitude
            )

            mapView.projection.toPixels(
                geoPoint,
                screenPoint
            )

            canvas.drawCircle(
                screenPoint.x.toFloat(),
                screenPoint.y.toFloat(),
                18f,
                paint
            )

            canvas.drawCircle(
                screenPoint.x.toFloat(),
                screenPoint.y.toFloat(),
                18f,
                borderPaint
            )
        }
    }

    override fun onSingleTapConfirmed(
        event: MotionEvent,
        mapView: MapView
    ): Boolean {

        val screenPoint = Point()
        val maxDistance = 55f

        for (point in points) {

            val geoPoint = GeoPoint(
                point.latitude,
                point.longitude
            )

            mapView.projection.toPixels(
                geoPoint,
                screenPoint
            )

            val dx = event.x - screenPoint.x
            val dy = event.y - screenPoint.y

            val distanceSquared = dx * dx + dy * dy

            if (distanceSquared <= maxDistance * maxDistance) {
                onPointClick(point.name)
                return true
            }
        }

        return false
    }
}

// --------------------------------------------------
// PANTALLA DEL MAPA
// --------------------------------------------------

private class FixedMapView(context: android.content.Context) :
    MapView(context) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}

@Composable
fun MapScreen(
    onPointClick: (String) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val appContext = context.applicationContext

    var touristPoints by remember {
        mutableStateOf<List<TouristPoint>>(emptyList())
    }

    var mapView by remember {
        mutableStateOf<MapView?>(null)
    }

    var locationOverlay by remember {
        mutableStateOf<MyLocationNewOverlay?>(null)
    }

    // Cargar puntos turísticos desde SQLite.
    LaunchedEffect(Unit) {
        touristPoints = withContext(Dispatchers.IO) {
            PlayerDatabaseHelper(appContext)
                .getTouristPoints()
                .filter { it.active }
        }
    }

    // Activar ubicación después de recibir permisos.
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                locationOverlay?.enableMyLocation(
                    GpsMyLocationProvider(context)
                )

                locationOverlay?.enableFollowLocation()
            }
        }

    // Mostrar ubicación del jugador.
    fun showMyLocation() {

        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {

            locationOverlay?.enableMyLocation(
                GpsMyLocationProvider(context)
            )

            locationOverlay?.enableFollowLocation()

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Ciclo de vida del mapa.
    val currentMapView = mapView

    DisposableEffect(currentMapView) {

        currentMapView?.onResume()

        onDispose {
            currentMapView?.onPause()
            currentMapView?.onDetach()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // ENCABEZADO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "Mapa de Santiago",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Explora los puntos turísticos y aprende sobre inteligencia artificial.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    top = 4.dp,
                    bottom = 12.dp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Volver",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = { showMyLocation() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Mi ubicación",
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }

        // MAPA OPENSTREETMAP
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            factory = { viewContext ->

                // Configuración de osmdroid.
                Configuration.getInstance().load(
                    viewContext,
                    android.preference.PreferenceManager
                        .getDefaultSharedPreferences(viewContext)
                )

                val config = Configuration.getInstance()

                config.userAgentValue =
                    "AprendoIAJugando/1.0 (Android; Duoc UC)"

                config.setDebugMode(true)
                config.setDebugMapTileDownloader(true)
                config.setDebugTileProviders(true)

                FixedMapView(viewContext).apply {

                    setTileSource(TileSourceFactory.MAPNIK)

                    setUseDataConnection(true)

                    setMultiTouchControls(true)

                    isTilesScaledToDpi = true

                    controller.setZoom(14.5)

                    controller.setCenter(
                        GeoPoint(-33.4390, -70.6490)
                    )

                    // Proveedor de ubicación.
                    val gpsProvider = GpsMyLocationProvider(viewContext)

                    val overlay = MyLocationNewOverlay(
                        gpsProvider,
                        this
                    )

                    overlays.add(overlay)

                    locationOverlay = overlay

                    mapView = this
                }
            },

            update = { map ->

                // Evitar duplicar los marcadores.
                map.overlays.removeAll {
                    it is TouristPointsOverlay
                }

                // Agregar los puntos turísticos.
                if (touristPoints.isNotEmpty()) {

                    map.overlays.add(
                        TouristPointsOverlay(
                            points = touristPoints,
                            onPointClick = onPointClick
                        )
                    )
                }

                map.invalidate()
            }
        )

        // ATRIBUCIÓN OPENSTREETMAP
        Text(
            text = "Mapa © OpenStreetMap contributors",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}
