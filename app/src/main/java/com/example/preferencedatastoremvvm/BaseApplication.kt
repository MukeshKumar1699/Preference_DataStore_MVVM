package com.example.preferencedatastoremvvm

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
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
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@HiltAndroidApp
class BaseApplication : Application() {

    private val MAX_FILE_SIZE = 4 * 1024 * 1024  // 4 MB
    private val MAX_FILES = 5  // Maximum number of log files
    private lateinit var logFile: File  // Reference to the current log file


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        //Initialize X-Log for external storage
//        initXLogExternalStorage()
        //Initialize X-Log for internal storage
        initXlog()


    }

    private fun initXLogExternalStorage() {
        // Initialize XLog before logging
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "log_$timestamp.txt"

        val filePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/PreferenceDataStoreMVVMLogs"
        val logDir = File(filePath)
        if (!logDir.exists()) {
            logDir.mkdirs()  // Create directory if it doesn't exist
        }

        // Manage log file retention: delete older files if more than 5 exist
        maintainMaxLogFiles(logDir, 3)

        val config = LogConfiguration.Builder()
            .logLevel(LogLevel.ALL)  // Set the log level
            .tag("XLog")  // Set a tag for logs
            .build()

        val androidPrinter: Printer = AndroidPrinter()  // Log to Android logcat
        val filePrinter = FilePrinter.Builder(filePath)
            .fileNameGenerator(ChangelessFileNameGenerator(fileName))  // File name for logs
            .flattener(ClassicFlattener())
            .build()

        // Initialize XLog with config, androidPrinter, and filePrinter
        XLog.init(config, androidPrinter, filePrinter)
    }

    // Function to maintain a maximum number of log files
    private fun maintainMaxLogFiles(directory: File, maxFiles: Int) {
        val files = directory.listFiles()  // Get all files in the directory

        if (files != null && files.size > maxFiles) {
            // Sort files by last modified date (oldest first)
            val sortedFiles = files.sortedBy { it.lastModified() }

            // Delete older files if there are more than maxFiles
            for (i in 0 until (files.size - maxFiles)) {
                sortedFiles[i].delete()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initXlog() {

        val config = LogConfiguration.Builder().logLevel(LogLevel.ALL).tag("XLog").build()

        val androidPrinter: Printer = AndroidPrinter()

        val filePrinter: Printer = FilePrinter.Builder(File(this.filesDir, "logs").path)
            .fileNameGenerator(ChangelessFileNameGenerator("${LocalDate.now()}.log"))
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

        XLog.init(config, androidPrinter, filePrinter)
    }


    // Function to get log file for the current day
    fun getDailyLogFile(): File {
        val logFileDir = File(filesDir, "logs")
        if (!logFileDir.exists()) {
            logFileDir.mkdir()
        }

        // Format the date for the log file name (e.g., log_2024-09-10.txt)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        return File(logFileDir, "log_$currentDate.txt")
    }

}


