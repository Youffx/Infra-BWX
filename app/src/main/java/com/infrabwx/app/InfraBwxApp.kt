package com.infrabwx.app

import android.app.Application
import com.infrabwx.app.data.preferences.AppPreferences

class InfraBwxApp : Application() {
    lateinit var preferences: AppPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        preferences = AppPreferences(this)
    }
}
