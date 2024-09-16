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
import java.util.Date
import java.util.Locale
import java.util.UUID

object LoggerHelper {

    private lateinit var tempLogFilePath: String
    private lateinit var logsDirectory: File
    private lateinit var tempLogFile: File

    private val config: LogConfiguration = LogConfiguration.Builder()
        .logLevel(LogLevel.ALL)
        .tag("XLog")
        .build()

    private val androidPrinter: Printer = AndroidPrinter()

    fun initLogger(context: Context, fileName: String) {

        logsDirectory = File(context.filesDir, "AppLogs")
        if (!logsDirectory.exists()) {
            logsDirectory.mkdirs()
        }

        tempLogFilePath = File(logsDirectory, "$fileName.log").absolutePath
        tempLogFile = File(tempLogFilePath)

        val filePrinter = FilePrinter.Builder(logsDirectory.path)
            .fileNameGenerator(ChangelessFileNameGenerator("$fileName.log"))
            .backupStrategy(CustomBackupStrategy()) // No backup of log files
            .flattener(ClassicFlattener())
            .writer(object : SimpleWriter() {
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
            })
            .build()

        // Initialize XLog with the file printer
        XLog.init(config, androidPrinter, filePrinter)

        // Log the start of the session
        XLog.i("Logging session started with temp file $fileName.log.")
    }

    private fun initLoggerWithRenamedFile(fileName: String) {

        val filePrinter = FilePrinter.Builder(logsDirectory.path)
            .fileNameGenerator(ChangelessFileNameGenerator("${fileName}.log"))
            .backupStrategy(CustomBackupStrategy()) // No backup of log files
            .flattener(ClassicFlattener())
            .build()

        XLog.init(config, androidPrinter, filePrinter)
        XLog.i("Logger re-initialized with new file: $fileName")
    }


    // Function to rename the log file by copying contents to a new file
    fun renameLogFile() {

        val newFileName = generateLogFileName(UUID.randomUUID().toString())
        val newLogFilePath = File(logsDirectory, "$newFileName.log").absolutePath
        val newLogFile = File(newLogFilePath)

        if (tempLogFile.exists()) {

            initLoggerWithRenamedFile(newFileName)
            copyLogFile(tempLogFile, newLogFile)
            val deleted = tempLogFile.delete()

            if (deleted) {
                XLog.i("Temp log file deleted successfully.")
            } else {
                XLog.e("Failed to delete temp log file.")
            }
        } else {
            XLog.e("Temp log file does not exist.")
        }
    }

    // Helper function to copy the content of the temp log file to the new file
    private fun copyLogFile(sourceFile: File, destFile: File) {
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        // Read content from the source file
        val content = sourceFile.readText()

        // Write content to the destination file
        destFile.writeText(content)

        XLog.i("Temp log file content copied to new log file: ${destFile.name}")
    }


    // Generates a log file name based on timestamp and UUID
    fun generateLogFileName(uuid: String): String {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return "log_$timestamp$uuid"
    }

}
