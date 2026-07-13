package com.infrabwx.app.ui.terms

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.infrabwx.app.ui.theme.PrimaryBlue
import com.infrabwx.app.ui.theme.PrimaryGreen

@Composable
fun TermsScreen(onAccepted: () -> Unit) {
    var isChecked by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ketentuan Hukum",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Undang-Undang dan Peraturan Terkait",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LawCard(
                number = "01",
                title = "UU No. 22 Tahun 2009",
                subtitle = "Lalu Lintas dan Angkutan Jalan",
                articles = listOf(
                    "Pasal 24 ayat (1)" to "Pemerintah bertanggung jawab atas penyelenggaraan jalan yang berkeselamatan dan berwawasan lingkungan.",
                    "Pasal 25" to "Setiap jalan untuk lalu lintas umum wajib dilengkapi rambu, marka, alat pemberi isyarat, dan penerangan jalan.",
                    "Pasal 26" to "Pemeliharaan dan pengawasan jalan merupakan tanggung jawab pemerintah sesuai kewenangannya."
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LawCard(
                number = "02",
                title = "PP No. 34 Tahun 2006",
                subtitle = "Tentang Jalan",
                articles = listOf(
                    "Pasal 2" to "Jalan sebagai prasarana distribusi berperan penting dalam pengembangan wilayah dan peningkatan hubungan antar wilayah.",
                    "Pasal 90" to "Masyarakat dapat berperan serta dalam penyelenggaraan jalan, termasuk pengawasan dan pemeliharaan.",
                    "Pasal 91" to "Peran serta masyarakat dilakukan melalui penyampaian laporan atau informasi kepada penyelenggara jalan."
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LawCard(
                number = "03",
                title = "PM No. 47 Tahun 2023",
                subtitle = "Penyelenggaraan Bidang LLAJ",
                articles = listOf(
                    "Standar Pelayanan" to "Mengatur standar pelayanan minimal penyelenggaraan bidang lalu lintas dan angkutan jalan.",
                    "Fasilitas" to "Termasuk pemeliharaan fasilitas perlengkapan jalan dan penerangan jalan umum.",
                    "Partisipasi" to "Masyarakat berhak mendapat informasi dan berpartisipasi dalam pengawasan fasilitas lalu lintas."
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Dengan melanjutkan, Anda menyatakan bahwa Anda telah membaca dan memahami ketentuan hukum di atas serta bersedia melaporkan infrastruktur secara bertanggung jawab sesuai dengan peraturan yang berlaku.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryGreen,
                        uncheckedColor = PrimaryBlue
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Saya sudah membaca dan memahami seluruh ketentuan di atas",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAccepted,
                enabled = isChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    disabledContainerColor = PrimaryGreen.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Lanjutkan",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun LawCard(
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PrimaryBlue, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
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

            Spacer(modifier = Modifier.height(12.dp))

            articles.forEachIndexed { index, (article, desc) ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Row(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Column {
                        Text(
                            text = article,
                            style = MaterialTheme.typography.bodyMedium,
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
