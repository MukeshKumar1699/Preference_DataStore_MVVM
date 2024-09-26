package com.example.preferencedatastoremvvm

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp
import java.util.UUID

@HiltAndroidApp
class BaseApplication : Application() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        LoggerHelper.initLogger(
            this,
            LoggerHelper.generateLogFileName(UUID.randomUUID().toString())
        )
    }

    companion object {
        // To control the logging process
        @Volatile
        var isLoggingPaused = false
    }

}


