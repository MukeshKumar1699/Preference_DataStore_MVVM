package com.example.preferencedatastoremvvm

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp
import java.util.Date
import java.util.Locale
import java.util.UUID

@HiltAndroidApp
class BaseApplication : Application() {

    private fun generateLogFileName(): String {
        val uuid = UUID.randomUUID().toString()
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return "log_$timestamp$uuid"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        LoggerHelper.initLogger(this, generateLogFileName())
    }

}


