package com.infrabwx.app.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

private const val BANYUWANGI_LAT = -8.2186
private const val BANYUWANGI_LNG = 114.3667
private const val CLUSTER_RADIUS_METERS = 20.0

private data class ReportCluster(
    val centroid: GeoPoint,
    val locations: List<ReportLocationItem>,
    val radiusMeters: Double
)

@Composable
fun MapSection(
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = false,
    onToggleFullScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { AppsScriptRepository() }
    val scope = rememberCoroutineScope()
    var locations by remember { mutableStateOf<List<ReportLocationItem>>(emptyList()) }
    var isSatellite by remember { mutableStateOf(false) }
    var selectedCluster by remember { mutableStateOf<ReportCluster?>(null) }

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
            mapView.overlays.clear()
            val reds = locations.filter { it.status != "green" }
            val greens = locations.filter { it.status == "green" }

            val clusters = buildClusters(reds, CLUSTER_RADIUS_METERS)
            for (cluster in clusters) {
                val color = AndroidColor.parseColor("#E53935")
                val nonGreenCount = cluster.locations.size

                if (cluster.locations.size == 1) {
                    val marker = Marker(mapView).apply {
                        position = cluster.centroid
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = createDot(color)
                        setInfoWindow(null)
                        setOnMarkerClickListener { _, _ ->
                            scope.launch { selectedCluster = cluster }
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                } else {
                    val circle = Polygon().apply {
                        points = Polygon.pointsAsCircle(cluster.centroid, cluster.radiusMeters + 20.0)
                        fillColor = AndroidColor.argb(35, 229, 57, 53)
                        strokeColor = AndroidColor.argb(100, 229, 57, 53)
                        strokeWidth = 2f
                    }
                    mapView.overlays.add(circle)

                    val marker = Marker(mapView).apply {
                        position = cluster.centroid
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        icon = createClusterDot(nonGreenCount)
                        setInfoWindow(null)
                        setOnMarkerClickListener { _, _ ->
                            scope.launch { selectedCluster = cluster }
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
            }

            for (loc in greens) {
                val green = AndroidColor.parseColor("#43A047")
                val marker = Marker(mapView).apply {
                    position = GeoPoint(loc.latitude, loc.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = createDot(green)
                    setInfoWindow(null)
                    setOnMarkerClickListener { _, _ ->
                        scope.launch {
                            selectedCluster = ReportCluster(
                                centroid = GeoPoint(loc.latitude, loc.longitude),
                                locations = listOf(loc),
                                radiusMeters = 0.0
                            )
                        }
                        true
                    }
                }
                mapView.overlays.add(marker)
            }

            mapView.invalidate()
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

    selectedCluster?.let { cluster ->
        ClusterLocationDialog(
            cluster = cluster,
            onDismiss = { selectedCluster = null },
            context = context
        )
    }
}

@Composable
private fun ClusterLocationDialog(
    cluster: ReportCluster,
    onDismiss: () -> Unit,
    context: Context
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val titleColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
    val bodyColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (cluster.locations.size == 1) cluster.locations[0].kecamatan
                       else "${cluster.locations.size} Laporan di Area Ini",
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                cluster.locations.forEachIndexed { index, loc ->
                    if (index > 0) {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                    }
                    if (!loc.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = loc.imageUrl,
                            contentDescription = "Foto",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    DetailRow("Kategori", formatCategory(loc.category), bodyColor)
                    Spacer(Modifier.height(2.dp))
                    DetailRow("Kecamatan", loc.kecamatan, bodyColor)
                    Spacer(Modifier.height(2.dp))
                    DetailRow("Latitude", loc.latitude.toString(), bodyColor)
                    Spacer(Modifier.height(2.dp))
                    DetailRow("Longitude", loc.longitude.toString(), bodyColor)
                    if (loc.status == "green") {
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF43A047).copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Sudah diperbaiki",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color.White else Color(0xFF2E7D32)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = { context.openInGoogleMaps(loc) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Buka Gmaps")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, color: Color) {
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color
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

private fun buildClusters(locations: List<ReportLocationItem>, maxDist: Double): List<ReportCluster> {
    val unvisited = locations.toMutableList()
    val result = mutableListOf<ReportCluster>()

    while (unvisited.isNotEmpty()) {
        val cluster = mutableListOf(unvisited.removeAt(0))
        var i = 0
        while (i < cluster.size) {
            val pivot = GeoPoint(cluster[i].latitude, cluster[i].longitude)
            val iter = unvisited.iterator()
            while (iter.hasNext()) {
                val candidate = iter.next()
                val dist = GeoPoint(candidate.latitude, candidate.longitude).distanceToAsDouble(pivot)
                if (dist <= maxDist) {
                    cluster.add(candidate)
                    iter.remove()
                }
            }
            i++
        }

        val avgLat = cluster.map { it.latitude }.average()
        val avgLng = cluster.map { it.longitude }.average()
        val centroid = GeoPoint(avgLat, avgLng)
        val radius = cluster.maxOf {
            GeoPoint(it.latitude, it.longitude).distanceToAsDouble(centroid)
        }
        result.add(ReportCluster(centroid, cluster, radius))
    }
    return result
}

private fun createDot(color: Int): android.graphics.drawable.Drawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setSize(48, 48)
        setColor(color)
        setStroke(4, AndroidColor.WHITE)
        setBounds(0, 0, 48, 48)
    }
}

private fun createClusterDot(count: Int): android.graphics.drawable.Drawable {
    return object : android.graphics.drawable.Drawable() {
        override fun draw(canvas: android.graphics.Canvas) {
            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
            val color = AndroidColor.parseColor("#E53935")

            paint.color = color
            paint.style = android.graphics.Paint.Style.FILL
            canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), 28f, paint)

            paint.color = AndroidColor.WHITE
            paint.style = android.graphics.Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), 28f, paint)

            paint.color = AndroidColor.WHITE
            paint.style = android.graphics.Paint.Style.FILL
            paint.textSize = 28f
            paint.textAlign = android.graphics.Paint.Align.CENTER
            val y = bounds.exactCenterY() - (paint.descent() + paint.ascent()) / 2f
            canvas.drawText(count.toString(), bounds.exactCenterX(), y, paint)
        }

        override fun getIntrinsicWidth() = 56
        override fun getIntrinsicHeight() = 56
        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}
        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int = android.graphics.PixelFormat.OPAQUE
    }.apply { setBounds(0, 0, 56, 56) }
}
