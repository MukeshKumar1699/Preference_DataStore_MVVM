package com.example.preferencedatastoremvvm

import android.content.Context
import android.os.Build
import com.elvishew.xlog.BuildConfig
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.naming.ChangelessFileNameGenerator
import com.elvishew.xlog.printer.file.writer.SimpleWriter
import java.io.File

object LoggerHelper {

    private lateinit var logFilePath: String


    fun initLogger(context: Context, fileName: String) {

        val logsDirectory = File(context.filesDir, "AppLogs")

        // Create the logs directory if it doesn't exist
        if (!logsDirectory.exists()) {
            logsDirectory.mkdirs()
        }

        val config = LogConfiguration.Builder().logLevel(LogLevel.ALL).tag("XLog").build()

        val androidPrinter: Printer = AndroidPrinter()

        val filePrinter = FilePrinter.Builder(logsDirectory.path)
            .fileNameGenerator(ChangelessFileNameGenerator("${fileName}.log"))
            .backupStrategy(CustomBackupStrategy(context))  // No backup of log files
            .flattener(ClassicFlattener()).writer(object : SimpleWriter() {
                override fun onNewFileCreated(file: File) {
                    super.onNewFileCreated(file)
                    val header = """             
         >>>>>>>>>>>>>>>> File Header >>>>>>>>>>>>>>>>
         Device Manufacturer: ${Build.MANUFACTURER}
         Device Model       : ${Build.MODEL}
         Android Version    : ${Build.VERSION.RELEASE}
         Android SDK        : ${Build.VERSION.SDK_INT}
         App VersionName    : ${BuildConfig.VERSION_NAME}
         App VersionCode    : ${BuildConfig.VERSION_CODE}
         <<<<<<<<<<<<<<<< File Header <<<<<<<<<<<<<<<< 
         """.trimIndent()
                    appendLog(header)
                }
            }).build()

        // Initialize XLog with the file printer
        XLog.init(config, androidPrinter, filePrinter)

        // Log the start of the session
        XLog.i("Logging session started.")
    }

    // Function to rename the log file
    fun renameLogFile(newFileName: String) {
        val logsDirectory = File(logFilePath).parentFile

        val newFile = File(logsDirectory, "$newFileName.log")
        val currentLogFile = File(logFilePath)

        if (currentLogFile.exists()) {
            val renamed = currentLogFile.renameTo(newFile)
            if (renamed) {
                logFilePath = newFile.absolutePath
                XLog.i("Log file renamed to $newFileName.log")
            } else {
                XLog.e("Failed to rename log file.")
            }
        }
    }

    // Function to close the logger at the end of the session
    fun closeLogger() {
        XLog.i("Logging session ended.")
        // Optional: You can add logic to close the logger if needed
    }
}
