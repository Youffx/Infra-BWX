package com.infrabwx.app.ui.camera

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.infrabwx.app.data.model.CategoryProvider
import com.infrabwx.app.ui.theme.DarkGreen
import com.infrabwx.app.ui.theme.PrimaryBlue
import com.infrabwx.app.ui.theme.PrimaryGreen
import com.infrabwx.app.ui.theme.TextSecondary
import com.infrabwx.app.util.ImageUtils
import com.infrabwx.app.util.isDevModeEnabled
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    categoryId: String,
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: CameraViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()
    val category = CategoryProvider.getCategory(categoryId)
    var showDevWarning by remember { mutableStateOf(false) }

    val cameraController = remember {
        LifecycleCameraController(context.applicationContext).apply {
            bindToLifecycle(lifecycleOwner)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraController.unbind()
        }
    }

    if (showDevWarning) {
        DevWarningDialog(
            onExit = {
                (context as? Activity)?.finishAffinity()
            },
            onDismiss = { showDevWarning = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        TopAppBar(
            title = {
                Text(
                    text = category?.name ?: "Kategori",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (context.isDevModeEnabled()) {
                        showDevWarning = true
                    } else {
                        viewModel.resetCapture()
                        onBack()
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            )
        )

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (!state.isCaptured) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            controller = cameraController
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (state.isLocationLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Mendapatkan lokasi...", color = Color.White)
                        }
                    }
                }
            } else {
                state.capturedBitmap?.let { bitmap ->
                    val watermarked = remember(bitmap, state.latitude, state.kecamatan) {
                        ImageUtils.drawWatermark(
                            bitmap = bitmap,
                            category = category?.name ?: categoryId,
                            latitude = state.latitude,
                            longitude = state.longitude,
                            kecamatan = state.kecamatan
                        )
                    }
                    Image(
                        bitmap = watermarked.asImageBitmap(),
                        contentDescription = "Captured photo",
                        modifier = Modifier.fillMaxSize()
                    )

                    if (!state.isInBanyuwangi && state.latitude != 0.0) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.padding(32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Lokasi Tidak Didukung",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE53935)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Anda tidak sedang di wilayah Kabupaten Banyuwangi. " +
                                                "Laporan hanya dapat dikirim dari wilayah Banyuwangi.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.isSubmitting) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mengirim laporan...", color = Color.White)
                }
            }
        }

        if (state.submitSuccess) {
            AlertDialog(
                onDismissRequest = { viewModel.clearError(); onSubmitted() },
                title = { Text("Berhasil", fontWeight = FontWeight.Bold, color = DarkGreen) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Laporan berhasil dikirim. Terima kasih atas partisipasi Anda.")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError(); onSubmitted() }) {
                        Text("OK", color = PrimaryGreen)
                    }
                }
            )
        }

        state.submitError?.let { error ->
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("Gagal", fontWeight = FontWeight.Bold, color = Color(0xFFE53935)) },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Tutup", color = PrimaryGreen)
                    }
                }
            )
        }

        BottomBar(
            isCaptured = state.isCaptured,
            isLocationLoading = state.isLocationLoading,
            canSubmit = state.isInBanyuwangi && state.latitude != 0.0,
            isSubmitting = state.isSubmitting,
                    onCapture = {
                if (context.isDevModeEnabled()) {
                    showDevWarning = true
                    return@BottomBar
                }
                val file = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                cameraController.takePicture(
                    outputOptions,
                    context.mainExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            if (bitmap != null) {
                                val rotated = rotateBitmapIfNeeded(bitmap, file.absolutePath)
                                viewModel.onImageCaptured(rotated)
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            // handle error
                        }
                    }
                )
            },
            onRetake = {
                if (context.isDevModeEnabled()) {
                    showDevWarning = true
                } else {
                    viewModel.resetCapture()
                }
            },
            onSubmit = {
                if (context.isDevModeEnabled()) {
                    showDevWarning = true
                } else {
                    viewModel.submitReport(categoryId)
                }
            }
        )
    }
}

@Composable
private fun BottomBar(
    isCaptured: Boolean,
    isLocationLoading: Boolean,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onCapture: () -> Unit,
    onRetake: () -> Unit,
    onSubmit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(16.dp)
    ) {
        if (!isCaptured) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = onCapture,
                    enabled = !isLocationLoading && !isSubmitting,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Ambil foto",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onRetake,
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Foto Ulang", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onSubmit,
                    enabled = canSubmit && !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        disabledContainerColor = PrimaryGreen.copy(alpha = 0.4f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kirim Laporan", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DevWarningDialog(onExit: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onExit) {
                Text("Keluar", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kembali", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        },
        icon = {
            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        },
        title = {
            Text("Mode Pengembang Terdeteksi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Text("Aplikasi ini tidak dapat berjalan ketika Mode Pengembang aktif. Silakan nonaktifkan Mode Pengembang di pengaturan perangkat Anda.", style = MaterialTheme.typography.bodyMedium)
        }
    )
}

private fun rotateBitmapIfNeeded(bitmap: android.graphics.Bitmap, filePath: String): android.graphics.Bitmap {
    try {
        val exif = androidx.exifinterface.media.ExifInterface(filePath)
        val orientation = exif.getAttributeInt(
            androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
        )
        val matrix = Matrix()
        when (orientation) {
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        if (matrix.isIdentity) return bitmap
        return android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
        return bitmap
    }
}
