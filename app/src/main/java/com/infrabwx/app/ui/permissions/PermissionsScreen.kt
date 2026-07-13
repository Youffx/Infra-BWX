package com.infrabwx.app.ui.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.infrabwx.app.ui.theme.DarkBlue
import com.infrabwx.app.ui.theme.PrimaryBlue

@Composable
fun PermissionsScreen(onPermissionsGranted: () -> Unit, onDenied: () -> Unit) {
    val context = LocalContext.current

    val cameraPermission = Manifest.permission.CAMERA
    val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }

    LaunchedEffect(Unit) {
        val cameraGranted = ContextCompat.checkSelfPermission(context, cameraPermission) ==
                PackageManager.PERMISSION_GRANTED
        val locationGranted = ContextCompat.checkSelfPermission(context, fineLocationPermission) ==
                PackageManager.PERMISSION_GRANTED

        if (cameraGranted && locationGranted) {
            onPermissionsGranted()
        } else {
            launcher.launch(
                arrayOf(cameraPermission, fineLocationPermission, coarseLocationPermission)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Akses Kamera",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DarkBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aplikasi memerlukan akses kamera untuk mengambil foto laporan",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Akses Lokasi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DarkBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aplikasi memerlukan akses lokasi untuk mengetahui posisi laporan",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
