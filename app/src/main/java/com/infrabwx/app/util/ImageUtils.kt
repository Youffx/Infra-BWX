package com.infrabwx.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun drawWatermark(
        bitmap: Bitmap,
        category: String,
        latitude: Double,
        longitude: Double,
        kecamatan: String
    ): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 36f
            isAntiAlias = true
            setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
        }

        val lines = listOf(
            "Kategori: $category",
            "Koordinat: ${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}",
            "Kecamatan: $kecamatan"
        )

        var y = bitmap.height - 20f
        for (line in lines.reversed()) {
            canvas.drawText(line, 20f, y, paint)
            y -= 48f
        }
        return result
    }

    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 80): String {
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
        val bytes = output.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun decodeBase64ToBitmap(base64: String): Bitmap {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
