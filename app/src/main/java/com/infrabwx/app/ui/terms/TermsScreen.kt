package com.infrabwx.app.ui.terms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.infrabwx.app.ui.theme.DarkBlue
import com.infrabwx.app.ui.theme.PrimaryBlue
import com.infrabwx.app.ui.theme.PrimaryGreen

@Composable
fun TermsScreen(onAccepted: () -> Unit) {
    var isChecked by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Ketentuan Hukum",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Undang-Undang dan Peraturan Terkait",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            TermsSection(
                title = "UU No. 22 Tahun 2009 tentang LLAJ",
                content = """
                    Undang-Undang No. 22 Tahun 2009 tentang Lalu Lintas dan Angkutan Jalan mengatur penyelenggaraan lalu lintas dan angkutan jalan yang aman, selamat, tertib, dan lancar.
                    
                    Pasal 24 ayat (1): Pemerintah bertanggung jawab atas penyelenggaraan jalan yang berkeselamatan dan berwawasan lingkungan.
                    
                    Pasal 25: Setiap jalan yang digunakan untuk lalu lintas umum wajib dilengkapi dengan perlengkapan jalan berupa rambu lalu lintas, marka jalan, alat pemberi isyarat lalu lintas, dan penerangan jalan.
                    
                    Pasal 26: Pemeliharaan dan pengawasan jalan merupakan tanggung jawab pemerintah sesuai dengan kewenangannya.
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TermsSection(
                title = "PP No. 34 Tahun 2006 tentang Jalan",
                content = """
                    Peraturan Pemerintah No. 34 Tahun 2006 tentang Jalan mengatur tentang jalan sebagai bagian dari sistem transportasi nasional.
                    
                    Pasal 2: Jalan sebagai prasarana distribusi memiliki peranan penting dalam mendukung pengembangan wilayah dan peningkatan hubungan antar wilayah.
                    
                    Pasal 5: Jalan umum dikelompokkan menurut sistem, fungsi, status, dan kelasnya.
                    
                    Pasal 90: Masyarakat dapat berperan serta dalam penyelenggaraan jalan, termasuk dalam pengawasan dan pemeliharaan.
                    
                    Pasal 91: Peran serta masyarakat dapat dilakukan melalui penyampaian laporan atau informasi kepada penyelenggara jalan.
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TermsSection(
                title = "PM No. 47 Tahun 2023",
                content = """
                    Peraturan Menteri No. 47 Tahun 2023 tentang Penyelenggaraan Bidang Lalu Lintas dan Angkutan Jalan.
                    
                    Mengatur tentang standar pelayanan minimal penyelenggaraan bidang lalu lintas dan angkutan jalan, termasuk pemeliharaan fasilitas perlengkapan jalan dan penerangan jalan umum.
                    
                    Masyarakat berhak mendapatkan informasi dan berpartisipasi dalam pengawasan penyelenggaraan fasilitas lalu lintas dan angkutan jalan.
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Dengan melanjutkan, Anda menyatakan bahwa Anda telah membaca dan memahami ketentuan hukum di atas serta bersedia melaporkan infastruktur secara bertanggung jawab sesuai dengan peraturan yang berlaku.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.foundation.layout.Row(
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
            Text(
                text = "Saya sudah membaca keseluruhan UU di atas",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
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
                disabledContainerColor = PrimaryGreen.copy(alpha = 0.4f)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Lanjutkan",
                style = MaterialTheme.typography.labelLarge,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}

@Composable
private fun TermsSection(title: String, content: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DarkBlue
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = content,
        style = MaterialTheme.typography.bodyMedium
    )
}
