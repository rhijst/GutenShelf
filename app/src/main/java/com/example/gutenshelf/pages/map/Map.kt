package com.example.gutenshelf.pages.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.gutenshelf.MainActivity
import com.example.gutenshelf.R
import com.example.gutenshelf.network.VolleySingleton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

private const val MAP_DEFAULT_ZOOM = 15.0
private const val LOCATION_ICON_SIZE_DP = 28
private const val LIBRARY_ICON_SIZE_DP = 15
private const val LIBRARY_SEARCH_RADIUS_M = 5000

private fun dpToPx(context: Context, dp: Int): Int =
    (dp * context.resources.displayMetrics.density).toInt()

private fun libraryOverpassUrl(lat: Double, lon: Double) =
    "https://overpass-api.de/api/interpreter?data=[out:json];" +
            "node[\"amenity\"=\"library\"](around:$LIBRARY_SEARCH_RADIUS_M,$lat,$lon);out;"

enum class PermissionState { UNKNOWN, GRANTED, DENIED, PERMANENTLY_DENIED }

private fun resolveInitialPermissionState(context: Context): PermissionState {
    val hasFine = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return if (hasFine || hasCoarse) PermissionState.GRANTED else PermissionState.UNKNOWN
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val activity = context as? MainActivity

    var permissionState by remember { mutableStateOf(resolveInitialPermissionState(context)) }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        permissionState = when {
            granted -> PermissionState.GRANTED
            activity != null && (
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    ) -> PermissionState.DENIED
            else -> PermissionState.PERMANENTLY_DENIED
        }
    }

    LaunchedEffect(Unit) {
        if (permissionState == PermissionState.UNKNOWN) {
            launcher.launch(locationPermissions)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (permissionState) {
            PermissionState.GRANTED -> OsmMapView()
            PermissionState.DENIED -> PermissionFallback(
                message = "Location permission is required to show your position on the map.",
                buttonText = "Grant Permission",
                onButtonClick = { launcher.launch(locationPermissions) }
            )
            PermissionState.PERMANENTLY_DENIED -> PermissionFallback(
                message = "Location permission has been permanently denied. " +
                        "Please enable it in the app settings to use map features.",
                buttonText = null,
                onButtonClick = {}
            )
            PermissionState.UNKNOWN -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun PermissionFallback(
    message: String,
    buttonText: String?,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        if (buttonText != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onButtonClick) { Text(buttonText) }
        }
    }
}

@Composable
fun OsmMapView() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context) }

    val myLocationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            val locationIconPx = dpToPx(context, LOCATION_ICON_SIZE_DP)
            ContextCompat.getDrawable(context, R.drawable.navigation)
                ?.toBitmap()
                ?.let { Bitmap.createScaledBitmap(it, locationIconPx, locationIconPx, true) }
                ?.also { icon ->
                    setDirectionArrow(icon, icon)
                    setPersonIcon(icon)
                    setPersonAnchor(0.5f, 0.5f)
                    setDirectionAnchor(0.5f, 0.5f)
                }
            enableMyLocation()
            enableFollowLocation()
        }
    }

    val libraryMarkers = remember { mutableStateListOf<Marker>() }

    LaunchedEffect(context) {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            userAgentValue = context.packageName
        }

        myLocationOverlay.runOnFirstFix {
            val location = myLocationOverlay.myLocation ?: return@runOnFirstFix
            fetchLibraries(context, location.latitude, location.longitude) { libraries ->
                mapView.post {
                    // Remove stale markers
                    libraryMarkers.forEach { mapView.overlays.remove(it) }
                    libraryMarkers.clear()

                    val libraryIconPx = dpToPx(context, LIBRARY_ICON_SIZE_DP)
                    val libraryIcon = ContextCompat.getDrawable(context, R.drawable.marker)
                        ?.toBitmap(width = libraryIconPx, height = libraryIconPx)
                        ?.toDrawable(context.resources)

                    libraries.forEach { lib ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(lib.lat, lib.lon)
                            title = lib.name
                            snippet = "Library"
                            icon = libraryIcon
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.add(marker)
                        libraryMarkers.add(marker)
                    }
                    mapView.invalidate()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    myLocationOverlay.enableMyLocation()
                    myLocationOverlay.enableFollowLocation()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                    myLocationOverlay.disableMyLocation()
                    myLocationOverlay.disableFollowLocation()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AndroidView(
        factory = {
            mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(MAP_DEFAULT_ZOOM)
                overlays.add(myLocationOverlay)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

data class Library(val name: String, val lat: Double, val lon: Double)

private fun fetchLibraries(
    context: Context,
    lat: Double,
    lon: Double,
    onResult: (List<Library>) -> Unit
) {
    val request = JsonObjectRequest(
        Request.Method.GET,
        libraryOverpassUrl(lat, lon),
        null,
        { response ->
            val elements = response.optJSONArray("elements")
            val libraries = buildList {
                if (elements != null) {
                    for (i in 0 until elements.length()) {
                        val element = elements.getJSONObject(i)
                        val name = element.optJSONObject("tags")?.optString("name") ?: "Unnamed Library"
                        add(Library(name, element.getDouble("lat"), element.getDouble("lon")))
                    }
                }
            }
            onResult(libraries)
        },
        { error -> Log.e("MapScreen", "Error fetching libraries: ${error.message}") }
    )

    VolleySingleton.getInstance(context).addToRequestQueue(request)
}
