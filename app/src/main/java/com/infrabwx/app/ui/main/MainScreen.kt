package com.infrabwx.app.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.infrabwx.app.data.model.CategoryProvider
import com.infrabwx.app.data.model.ReportCategory
import com.infrabwx.app.data.preferences.AppPreferences
import com.infrabwx.app.ui.theme.PrimaryBlue
import com.infrabwx.app.util.isDevModeEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private data class CategoryMeta(
    val icon: ImageVector,
    val desc: String
)

private val categoryMeta = mapOf(
    "jalan_rusak" to CategoryMeta(Icons.Default.Place, "Laporkan kerusakan jalan berlubang, retak, atau ambles"),
    "tambalan_tidak_rata" to CategoryMeta(Icons.Default.Build, "Laporkan tambalan jalan yang tidak rata atau mengganggu"),
    "lampu_mati" to CategoryMeta(Icons.Default.Star, "Laporkan penerangan jalan umum yang padam atau rusak"),
    "takedown" to CategoryMeta(Icons.Default.Delete, "Ajukan permohonan penghapusan laporan yang sudah masuk")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onCategoryClick: (String) -> Unit,
    themeMode: String,
    preferences: AppPreferences
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(themeMode) }
    var showDevWarning by remember { mutableStateOf(false) }
    var isMapFullScreen by remember { mutableStateOf(false) }

    if (showDevWarning) {
        DevWarningDialog(
            onExit = {
                (context as? Activity)?.finishAffinity()
            },
            onDismiss = { showDevWarning = false }
        )
    }

    if (isMapFullScreen) {
        val activity = LocalContext.current as? Activity
        DisposableEffect(Unit) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            activity?.window?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
            onDispose {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            MapSection(
                modifier = Modifier.fillMaxSize(),
                isFullScreen = true,
                onToggleFullScreen = { isMapFullScreen = false }
            )
            IconButton(
                onClick = { isMapFullScreen = false },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali"
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Infra BWX",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                },
                actions = {
                    IconButton(onClick = {
                        if (context.isDevModeEnabled()) {
                            showDevWarning = true
                        } else {
                            showMenu = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ketentuan Hukum", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                if (context.isDevModeEnabled()) {
                                    showMenu = false
                                    showDevWarning = true
                                } else {
                                    showMenu = false
                                    showTermsDialog = true
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Developer", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                if (context.isDevModeEnabled()) {
                                    showMenu = false
                                    showDevWarning = true
                                } else {
                                    showMenu = false
                                    showCreditsDialog = true
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mode Tampilan", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                if (context.isDevModeEnabled()) {
                                    showMenu = false
                                    showDevWarning = true
                                } else {
                                    showMenu = false
                                    selectedTheme = themeMode
                                    showThemeDialog = true
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            Text(
                text = "Pilih Kategori Laporan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(CategoryProvider.categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = {
                            if (context.isDevModeEnabled()) {
                                showDevWarning = true
                            } else {
                                onCategoryClick(category.id)
                            }
                        }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.height(240.dp)) {
                    MapSection(
                        modifier = Modifier.fillMaxSize(),
                        isFullScreen = false,
                        onToggleFullScreen = { isMapFullScreen = true }
                    )
                }
            }
        }
    }

    if (showTermsDialog) {
        TermsDialog(onDismiss = { showTermsDialog = false })
    }

    if (showCreditsDialog) {
        CreditsDialog(onDismiss = { showCreditsDialog = false })
    }

    if (showThemeDialog) {
        ThemeModeDialog(
            currentMode = selectedTheme,
            onSelect = { mode ->
                selectedTheme = mode
                showThemeDialog = false
                CoroutineScope(Dispatchers.IO).launch {
                    preferences.setThemeMode(mode)
                }
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
private fun CategoryCard(category: ReportCategory, onClick: () -> Unit) {
    val meta = categoryMeta[category.id] ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(category.color).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = meta.icon,
                    contentDescription = category.name,
                    modifier = Modifier.size(28.dp),
                    tint = Color(category.color)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meta.desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun TermsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = PrimaryBlue)
            }
        },
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ketentuan Hukum",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Undang-Undang dan Peraturan Terkait",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LawDialogCard(
                    number = "01",
                    title = "UU No. 22 Tahun 2009",
                    subtitle = "Lalu Lintas dan Angkutan Jalan",
                    articles = listOf(
                        "Pasal 24 ayat (1)" to "Pemerintah bertanggung jawab atas penyelenggaraan jalan yang berkeselamatan dan berwawasan lingkungan.",
                        "Pasal 25" to "Setiap jalan untuk lalu lintas umum wajib dilengkapi rambu, marka, alat pemberi isyarat, dan penerangan jalan.",
                        "Pasal 26" to "Pemeliharaan dan pengawasan jalan merupakan tanggung jawab pemerintah sesuai kewenangannya."
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LawDialogCard(
                    number = "02",
                    title = "PP No. 34 Tahun 2006",
                    subtitle = "Tentang Jalan",
                    articles = listOf(
                        "Pasal 2" to "Jalan sebagai prasarana distribusi berperan penting dalam pengembangan wilayah.",
                        "Pasal 90" to "Masyarakat dapat berperan serta dalam penyelenggaraan jalan, termasuk pengawasan.",
                        "Pasal 91" to "Peran serta masyarakat dilakukan melalui penyampaian laporan kepada penyelenggara jalan."
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LawDialogCard(
                    number = "03",
                    title = "PM No. 47 Tahun 2023",
                    subtitle = "Penyelenggaraan Bidang LLAJ",
                    articles = listOf(
                        "Standar Pelayanan" to "Mengatur standar pelayanan minimal penyelenggaraan bidang lalu lintas.",
                        "Fasilitas" to "Termasuk pemeliharaan fasilitas perlengkapan jalan dan penerangan jalan.",
                        "Partisipasi" to "Masyarakat berhak mendapat informasi dan berpartisipasi dalam pengawasan."
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryBlue.copy(alpha = 0.06f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Dengan menggunakan aplikasi ini, Anda turut berpartisipasi dalam pengawasan dan pelaporan infrastruktur sesuai dengan peraturan perundang-undangan yang berlaku.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun LawDialogCard(
    number: String,
    title: String,
    subtitle: String,
    articles: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(PrimaryBlue, RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            articles.forEachIndexed { index, (article, desc) ->
                if (index > 0) Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.padding(start = 4.dp)) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Column {
                        Text(
                            text = article,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreditsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = PrimaryBlue)
            }
        },
        title = {
            Text(
                text = "Developer",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Telegram",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/inisahaf"))
                            context.startActivity(intent)
                        }
                        .background(PrimaryBlue.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Telegram",
                        modifier = Modifier.size(20.dp),
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "@YouffX",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ketuk untuk membuka Telegram",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    )
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
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        },
        title = {
            Text("Mode Pengembang Terdeteksi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Text("Aplikasi ini tidak dapat berjalan ketika Mode Pengembang aktif. Silakan nonaktifkan Mode Pengembang di pengaturan perangkat Anda.", style = MaterialTheme.typography.bodyMedium)
        }
    )
}

@Composable
private fun ThemeModeDialog(
    currentMode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "auto" to "Otomatis",
        "light" to "Terang",
        "dark" to "Gelap"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        },
        title = {
            Text(
                text = "Mode Tampilan",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(value) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentMode == value,
                            onClick = { onSelect(value) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryBlue
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}
