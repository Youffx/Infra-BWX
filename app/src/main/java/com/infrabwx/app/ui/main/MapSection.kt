package com.infrabwx.app.ui.main

import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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

    LaunchedEffect(Unit) {
        val result = repository.getReportLocations()
        locations = result.getOrDefault(emptyList())
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
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    false
                }
            }
        }

        LaunchedEffect(locations) {
            mapView.overlays.removeAll { it is Marker }
            for (loc in locations) {
                val geo = GeoPoint(loc.latitude, loc.longitude)
                val marker = Marker(mapView).apply {
                    position = geo
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = redDotIcon
                    title = "${loc.kecamatan} - ${loc.category}"
                }
                mapView.overlays.add(marker)
            }
        }

        LaunchedEffect(isSatellite) {
            mapView.setTileSource(
                if (isSatellite) {
                    XYTileSource(
                        "Satellite",
                        3, 20, 256, ".png",
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

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onToggleFullScreen,
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = if (isFullScreen) "Ciutkan" else "Fullscreen",
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { isSatellite = !isSatellite },
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (isSatellite) PrimaryBlue.copy(alpha = 0.9f)
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        CircleShape
                    ),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (isSatellite) Color.White else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = if (isSatellite) Icons.Default.SatelliteAlt else Icons.Default.Layers,
                    contentDescription = if (isSatellite) "Peta" else "Satelit",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private val redDotIcon: android.graphics.drawable.Drawable by lazy {
    GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setSize(48, 48)
        setColor(android.graphics.Color.parseColor("#E53935"))
        setStroke(4, android.graphics.Color.WHITE)
    }
}
