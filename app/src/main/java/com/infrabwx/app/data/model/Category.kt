package com.infrabwx.app.data.model

data class ReportCategory(
    val id: String,
    val name: String,
    val icon: String,
    val color: Long
)

object CategoryProvider {
    val categories = listOf(
        ReportCategory(
            id = "jalan_rusak",
            name = "Jalan Rusak",
            icon = "road_variant",
            color = 0xFF1976D2
        ),
        ReportCategory(
            id = "tambalan_tidak_rata",
            name = "Tambalan Tidak Rata",
            icon = "road_bump",
            color = 0xFF4CAF50
        ),
        ReportCategory(
            id = "lampu_mati",
            name = "Lampu Jalan Padam",
            icon = "lightbulb_off",
            color = 0xFFFFA000
        ),
        ReportCategory(
            id = "takedown",
            name = "Takedown Laporan",
            icon = "delete_sweep",
            color = 0xFFE53935
        )
    )

    fun getCategory(id: String): ReportCategory? = categories.find { it.id == id }
}
