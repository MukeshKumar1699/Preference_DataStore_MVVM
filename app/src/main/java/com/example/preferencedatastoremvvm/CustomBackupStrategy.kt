package com.example.preferencedatastoremvvm

import android.content.Context
import android.util.Log
import com.elvishew.xlog.printer.file.backup.BackupStrategy
import java.io.File

class CustomBackupStrategy(private val context: Context) : BackupStrategy {

    fun deleteOldLogFilesByday(maxDays: Int) {
        val logsDirectory = File(context.filesDir, "logs")
        val currentTime = System.currentTimeMillis()

        if (logsDirectory.exists() && logsDirectory.isDirectory) {
            val logFiles = logsDirectory.listFiles { _, name ->
                name.endsWith(".log")
            }

            logFiles?.forEach { file ->
                val fileTime = file.lastModified()
                val daysOld = (currentTime - fileTime) / (1000 * 60 * 60 * 24)

                Log.d("LogCleanup", "Checking file: ${file.name}, Days old: $daysOld")

                if (daysOld > maxDays) {
                    val deleted = file.delete()
                    Log.d("LogCleanup", "File ${file.name} deleted: $deleted")
                } else {
                    Log.d("LogCleanup", "File ${file.name} kept")
                }

            }
        } else {
            Log.d("LogCleanup", "Logs directory does not exist or is not a directory")
        }
    }


    override fun shouldBackup(file: File?): Boolean {
        return false
    }
}

