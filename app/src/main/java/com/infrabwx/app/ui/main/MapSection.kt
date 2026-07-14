package com.infrabwx.app.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.infrabwx.app.data.model.ReportLocationItem
import com.infrabwx.app.data.remote.AppsScriptRepository
import com.infrabwx.app.ui.theme.PrimaryBlue
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val BANYUWANGI_LAT = -8.2186
private const val BANYUWANGI_LNG = 114.3667

@Composable
fun MapSection(
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = false,
    onToggleFullScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { AppsScriptRepository() }
    var locations by remember { mutableStateOf<List<ReportLocationItem>>(emptyList()) }
    var isSatellite by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<ReportLocationItem?>(null) }

    LaunchedEffect(Unit) {
        val result = repository.getReportLocations()
        if (result.isSuccess) {
            locations = result.getOrDefault(emptyList())
        }
    }

    Box(modifier = modifier) {
        val mapView = remember {
            Configuration.getInstance().apply {
                userAgentValue = context.packageName
            }
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(10.5)
                controller.setCenter(GeoPoint(BANYUWANGI_LAT, BANYUWANGI_LNG))
                setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        v.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    false
                }
            }
        }

        LaunchedEffect(locations) {
            mapView.overlays.removeAll { it is Marker }
            for (loc in locations) {
                val dot = if (loc.status == "green") createGreenDot() else createRedDot()
                val marker = Marker(mapView).apply {
                    position = GeoPoint(loc.latitude, loc.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = dot
                    title = "${loc.kecamatan} - ${loc.category}"
                    setInfoWindow(null)
                    setOnMarkerClickListener { _, _ ->
                        selectedLocation = loc
                        true
                    }
                }
                mapView.overlays.add(marker)
            }
            if (locations.isNotEmpty()) {
                mapView.invalidate()
            }
        }

        LaunchedEffect(isSatellite) {
            mapView.setTileSource(
                if (isSatellite) {
                    XYTileSource(
                        "Satellite",
                        3, 20, 256, "",
                        arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile")
                    )
                } else {
                    TileSourceFactory.MAPNIK
                }
            )
            mapView.invalidate()
        }

        DisposableEffect(Unit) {
            mapView.onResume()
            onDispose { mapView.onPause() }
        }

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onToggleFullScreen,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = if (isFullScreen) "Ciutkan" else "Fullscreen",
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = { isSatellite = !isSatellite },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSatellite) PrimaryBlue.copy(alpha = 0.9f)
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (isSatellite) Color.White else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = if (isSatellite) Icons.Default.SatelliteAlt else Icons.Default.Layers,
                    contentDescription = if (isSatellite) "Peta" else "Satelit",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }

    selectedLocation?.let { loc ->
        if (isFullScreen) {
            FullscreenLocationDialog(
                location = loc,
                onDismiss = { selectedLocation = null },
                context = context
            )
        } else {
            CompactLocationDialog(
                location = loc,
                onDismiss = { selectedLocation = null },
                context = context
            )
        }
    }
}

@Composable
private fun CompactLocationDialog(
    location: ReportLocationItem,
    onDismiss: () -> Unit,
    context: Context
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(location.kecamatan, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                DetailRow("Kategori", formatCategory(location.category))
                Spacer(Modifier.height(4.dp))
                DetailRow("Latitude", location.latitude.toString())
                Spacer(Modifier.height(4.dp))
                DetailRow("Longitude", location.longitude.toString())
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Tutup")
                }
                Button(
                    onClick = { context.openInGoogleMaps(location) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Buka Gmaps")
                }
            }
        }
    )
}

@Composable
private fun FullscreenLocationDialog(
    location: ReportLocationItem,
    onDismiss: () -> Unit,
    context: Context
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(location.kecamatan, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (!location.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = location.imageUrl,
                        contentDescription = "Foto lokasi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(12.dp))
                }
                DetailRow("Kategori", formatCategory(location.category))
                Spacer(Modifier.height(4.dp))
                DetailRow("Latitude", location.latitude.toString())
                Spacer(Modifier.height(4.dp))
                DetailRow("Longitude", location.longitude.toString())
                if (location.status == "green") {
                    Spacer(Modifier.height(4.dp))
                    DetailRow("Status", "Terverifikasi")
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Tutup")
                }
                Button(
                    onClick = { context.openInGoogleMaps(location) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Buka Gmaps")
                }
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatCategory(category: String): String {
    return category.split("_").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}

private fun Context.openInGoogleMaps(loc: ReportLocationItem) {
    val uri = "geo:${loc.latitude},${loc.longitude}?q=${loc.latitude},${loc.longitude}(${Uri.encode(loc.kecamatan)})"
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
}

private fun createRedDot(): android.graphics.drawable.Drawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setSize(48, 48)
        setColor(android.graphics.Color.parseColor("#E53935"))
        setStroke(4, android.graphics.Color.WHITE)
        setBounds(0, 0, 48, 48)
    }
}

private fun createGreenDot(): android.graphics.drawable.Drawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setSize(48, 48)
        setColor(android.graphics.Color.parseColor("#43A047"))
        setStroke(4, android.graphics.Color.WHITE)
        setBounds(0, 0, 48, 48)
    }
}
