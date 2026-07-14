package com.infrabwx.app.util

import android.content.Context
import android.provider.Settings

fun Context.isDevModeEnabled(): Boolean {
    return try {
        Settings.Global.getInt(
            contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) != 0
    } catch (_: Exception) {
        false
    }
}
